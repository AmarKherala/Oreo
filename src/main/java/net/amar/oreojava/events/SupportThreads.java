package net.amar.oreojava.events;

import net.amar.oreojava.Log;
import net.amar.oreojava.Oreo;
import net.amar.oreojava.Util;
import net.amar.oreojava.handlers.UrlRequest;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.ForumChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.events.channel.ChannelCreateEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.json.JSONArray;


import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SupportThreads extends ListenerAdapter {
    boolean isSupported;
    @Override
    public void onChannelCreate(ChannelCreateEvent event){
        if(!event.getChannelType().isThread() || Oreo.getSupportChannel()==null) return;

        ThreadChannel channel=event.getChannel().asThreadChannel();

        if(channel.getParentChannel() instanceof ForumChannel forum
                &&forum.getId().equals(Oreo.getSupportChannel().getId())){
            channel.retrieveStartMessage().queue(msg->{
                boolean hasLog=msg.getAttachments().stream()
                        .anyMatch(log->log.getFileName().contains("log"));

                if(!hasLog) msg.replyEmbeds(noLog().build()).queue();
                else parseLog(msg);
            });
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event){

        if(event.getChannelType().isThread() && Oreo.getSupportChannel()!=null){
            ThreadChannel channel=event.getChannel().asThreadChannel();

            if(channel.getParentChannel() instanceof ForumChannel forum
                    &&forum.getId().equals(Oreo.getSupportChannel().getId())){

                boolean isThreadOwner=channel.getOwnerIdLong()==event.getAuthor().getIdLong();

                if(isThreadOwner&&event.getMessageIdLong()!=channel.getIdLong()&&
                        event.getMessage().getAttachments().stream()
                                .anyMatch(log->log.getFileName().contains("log"))){
                    parseLog(event.getMessage());
                }

                event.getGuild().retrieveMember(event.getAuthor()).queue(member->{
                    boolean isAuthorStaff;
                    boolean isStaffPinged=false;
                    isAuthorStaff=member.getPermissions().contains(Permission.BAN_MEMBERS);

                    for(Member mem : event.getMessage().getMentions().getMembers()){
                        if(event.getMessage().getReferencedMessage()!=null&&
                                mem.getId().equals(event.getMessage().getReferencedMessage().getAuthor().getId())){
                            continue;
                        }
                        isStaffPinged = mem.getPermissions().contains(Permission.BAN_MEMBERS);
                    }
                    if(!isAuthorStaff && isStaffPinged)
                        event.getMessage().reply("Don't ping staff or you will get punished.").queue();
                });
            }
        }
    }

    private void parseLog(Message msg) {
        // Find latestlog.txt specifically
        Message.Attachment log = msg.getAttachments().stream()
                .filter(a -> a.getFileName().contains("log"))
                .findFirst()
                .orElse(null);

        if (log == null) {
            // If they sent attachments but none were latestlog.txt, still warn them
            if (!msg.getAttachments().isEmpty()) {
                msg.replyEmbeds(notMojoLog().build()).queue();
            }
            return;
        }

        String logString = UrlRequest.fetchMojoLog(log.getUrl());
        if (logString == null) {
            msg.reply("Couldn't parse your log.").queue();
            Log.warn("Failed to parse [latestlog.txt] from user [" + msg.getAuthor().getName() + "] in channel [" + msg.getChannel().getName() + "]");
            return;
        }

        String[] logContent = logString.split("\n");

        // first loop through the mod list
        isSupported = true;
        modListLoop(msg, logContent, logString);
        if (!isSupported) return;

        boolean isMojoLog = false;
        // here begins the parsing process
        String mojoVersion = "";
        String deviceModel = "";
        String deviceGPU = "";
        String mojoRenderer = "";
        String deviceArch = "";
        String ramAllocated = "";
        String mcVersion = "";
        String javaVersion = "";

        for (String line : logContent) {

            if (isMojoLog) continue;
            else isMojoLog = line.contains("git.artdeell.mojo") || line.contains("git.artdeell.mojo.debug");

            if (line.startsWith("Info: Launcher version:"))
                mojoVersion = line.substring("Info: Launcher version:".length()).trim();
            if (line.startsWith("Info: Architecture:"))
                deviceArch = line.substring("Info: Architecture:".length()).trim();
            if (line.startsWith("Info: Device model:"))
                deviceModel = line.substring("Info: Device model:".length()).trim();
            if (line.startsWith("Info: Selected Minecraft version:"))
                mcVersion = line.substring("Info: Selected Minecraft version:".length()).trim();
            if (line.startsWith("Info: Graphics device:"))
                deviceGPU = line.substring("Info: Graphics device:".length()).trim();
            if (line.startsWith("Info: RAM allocated:"))
                ramAllocated = line.substring("Info: RAM allocated:".length()).trim();
            if (line.startsWith("Added custom env: JAVA_HOME=/data/user/0/git.artdeell.mojo/runtimes/"))
                javaVersion = line.substring("Added custom env: JAVA_HOME=/data/user/0/git.artdeell.mojo/runtimes/".length()).trim();
            if (line.startsWith("Added custom env: JAVA_HOME=/data/user/0/git.artdeell.mojo.debug/runtimes/"))
                javaVersion = line.substring("Added custom env: JAVA_HOME=/data/user/0/git.artdeell.mojo.debug/runtimes/".length()).trim();
            if (line.startsWith("Added custom env: MOJO_RENDERER="))
                mojoRenderer = line.substring("Added custom env: MOJO_RENDERER=".length()).trim();
        }

        if (!isMojoLog) {
          msg.replyEmbeds(notMojoLog().build()).queue();
          return;
        }

        if (mojoVersion.isEmpty() || deviceModel.isEmpty()) {
            EmbedBuilder em = new EmbedBuilder();
            em.setTitle("Invalid log!");
            em.setDescription("The log you provided doesn't include the launcher version or device model");
            em.setColor(Color.cyan);
            msg.replyEmbeds(em.build()).queue();
            return;
        }

        EmbedBuilder em = new EmbedBuilder();
        em.setTitle("**Log information**");
        em.setDescription("**Mojo version:**\n" + mojoVersion
                + "\n**Device model:**\n" + deviceModel
                + "\n**Device GPU:**\n" + deviceGPU
                + "\n**Device architecture:**\n" + deviceArch
                + "\n**Mojo renderer:**\n" + mojoRenderer
                + "\n**Allocated RAM:**\n" + ramAllocated
                + "\n**Java Runtime:**\n" + javaVersion
                + "\n**Minecraft version:**\n" + mcVersion);
        em.setColor(Color.CYAN);
        msg.replyEmbeds(em.build()).queue();
    }

    private void modListLoop(Message msg , String[] logContent, String logString) {

        JSONArray notSupportedModsArray=Util.getBlacklistedMods();

        Pattern pattern= Pattern.compile("Loading\\s+\\d+\\s+mods:");
        String endMark="[main/INFO]: SpongePowered MIXIN Subsystem";

        StringBuilder mods=new StringBuilder();
        boolean insideModList=false;
        String[] mc17AndAbove = {"1.17","1.18","1.19","1.20","1.21"};
        boolean isMc17Above = false;
        boolean isSodium = false;

        for (String line : logContent) {
            Matcher matcher = pattern.matcher(line);

            if (matcher.find()) insideModList = true;
            if (line.contains(endMark)) insideModList = false;
            if (insideModList && notSupportedModsArray != null) {

                for (int i = 0; i < notSupportedModsArray.length(); i++) {
                    String modName = notSupportedModsArray.getString(i);
                    if (line.contains(modName)) {
                        isSupported = false;
                        mods.append(modName).append("\n");
                    }
                }

                for (String version : mc17AndAbove) {
                    if (logString.contains(version)) {
                        isMc17Above = true;
                        break;
                    }
                }

                if (logString.contains("opengles2") && line.contains("sodium") && isMc17Above) {
                    isSodium = true;
                }
            }
        }


        if (isSodium) {
            msg.replyEmbeds(gl4esWithSodium().build()).queue();
        }
        if(!(mods.isEmpty() && isSupported)){
            EmbedBuilder em=new EmbedBuilder();
            em.setTitle("Blacklisted mods detected ");
            em.setDescription("Blacklisted mod/s found: \n"+mods);
            em.setFooter("This post will be locked");

            ThreadChannel ch=msg.getChannel().asThreadChannel();
            msg.replyEmbeds(em.build()).queue();
            ch.getManager().setName("[BLACKLISTED] detected unsupported mods").setLocked(true).queue();
        }
    }

    private EmbedBuilder noLog(){
        EmbedBuilder em=new EmbedBuilder();
        em.setTitle("No log provided!");
        em.setDescription("**You didn't provide a log**. please read [this](https://discord.com/channels/1365346109131722753/1390045622924738651/1390045622924738651) if you dont know what is a log ");
        em.setColor(Color.RED);
        return em;
    }

    private EmbedBuilder notMojoLog(){
        EmbedBuilder em=new EmbedBuilder();
        em.setTitle("Not a mojo log");
        em.setDescription("The log you provided isnt a mojo log");
        em.setColor(Color.RED);
        return em;
    }

    private EmbedBuilder gl4esWithSodium() {
        return  new EmbedBuilder()
                .setTitle("sodium with GL4ES")
                .setDescription("Its recommended that you use sodium alongside LTW on version above 1.17.\n if you don't know what LTW is do !LTW")
                .setColor(Color.CYAN);
    }
}
