//Nolan Amblard did this
package Commands;

import Bot.Commands;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class UserInfo implements Commands {

    @Override
    public void execute(List<String> args, MessageReceivedEvent event) {
        String [] message = event.getMessage().getContentRaw().split(" ");
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        try {
            if(message.length == 1) {
                //returns info about the person who sent the message
                String userName = event.getMessage().getAuthor().getName();

                User user = event.getMessage().getMember().getUser();
                String avatar = user.getAvatarUrl();
                EmbedBuilder ae1 = new EmbedBuilder();

                ae1.setTitle(userName + "'s Profile");
                ae1.setThumbnail(avatar);
                ae1.addField("User name: ", event.getMessage().getAuthor().getAsMention(), true);
                ae1.addField("Online Status: ", event.getMessage().getMember().getOnlineStatus().toString(), true);
                ae1.setFooter("Request was made at " + formatter.format(date), event.getGuild().getIconUrl());
                ae1.setColor(Color.CYAN);
                event.getChannel().sendMessage(ae1.build()).queue();
            }
            else if(message.length == 2){
                //returns info about the person mentioned in the message
                User user = event.getMessage().getMentionedUsers().get(0);
                String userName = event.getGuild().getMember(event.getMessage().getMentionedUsers().get(0)).getEffectiveName();
                String avatar = user.getAvatarUrl();
                EmbedBuilder ae2 = new EmbedBuilder();

                ae2.setTitle(userName + "'s Profile");
                ae2.setThumbnail(avatar);
                ae2.addField("User name: ", event.getMessage().getMentionedUsers().get(0).getName(), true);
                ae2.addField("Online Status: ", event.getGuild().getMember(user).getOnlineStatus().toString(), true);
                ae2.setFooter("Request was made at " + formatter.format(date), event.getGuild().getIconUrl());
                ae2.setColor(Color.CYAN);
                event.getChannel().sendMessage(ae2.build()).queue();
            }
        }
        catch (NullPointerException e) {
            event.getChannel().sendMessage("An error occurred.").queue();
        }
    }

    @Override
    public String getKeyword() {
        return "UserInfo";
    }
}
