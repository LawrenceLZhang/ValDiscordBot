//Lawrence Zhang
package Commands;

import Bot.Commands;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import java.time.LocalDateTime;
import java.util.*;

public class SetTimer implements Commands {

    //Execution of the message
    @Override
    public void execute(List<String> args, MessageReceivedEvent event) {

        Message m = event.getMessage();
        User author = m.getAuthor();
        String[] content = m.getContentRaw().toLowerCase().split(" ");
        MessageChannel channel = event.getChannel();
        JDA jda = event.getJDA();
        User val = jda.getSelfUser();

        try {
            if (content.length == 2) {

                try {
                    String message = content[1];
                    Integer.parseInt(message.substring(0, 2));
                    Integer.parseInt(message.substring(3, 5));
                    Integer.parseInt(message.substring(6, 8));

                    if (message.charAt(2) != ':' && message.charAt(5) != ':') {
                        channel.sendMessage("The message contains invalid characters.").queue();

                        return;
                    }
                }
                catch (NumberFormatException | NullPointerException e) {
                    channel.sendMessage("The message contains invalid characters.").queue();

                    return;
                }

                String[] userTimes = content[1].split(":");

                LocalDateTime time = LocalDateTime.now();

                String hours = Integer.toString(time.getHour());
                String minutes = Integer.toString(time.getMinute());
                String seconds = Integer.toString(time.getSecond());

                String[] currentTimes = new String[3];
                currentTimes[0] = hours;
                currentTimes[1] = minutes;
                currentTimes[2] = seconds;

                int startTime = getTotalTime(currentTimes);
                int endTime = startTime + getTotalTime(userTimes);

                //Troubleshooting
//                System.out.println(startTime);
//                System.out.println(endTime);
//                System.out.println(currentTime);

                String timer = createTimer(author, channel, val, startTime, endTime);

                if (timer.equalsIgnoreCase("start")) {
                    return;
                }
                else if (timer.equalsIgnoreCase("stop")) {
                    return;
                }
            }
            else if (content.length > 2) {

            }
        }
        catch (InterruptedException e) {
            channel.sendMessage("Oops! Something went wrong.").queue();
            channel.sendMessage("The error received is: " + e.getMessage()).queue();
        }
    }

    @Override
    public String getKeyword() {
        return "SetTimer";
    }

    private String createTimer(User author, MessageChannel channel, User self, int startTime, int endTime) throws InterruptedException {
        
        int currentTime;
        
        for (int i = endTime; i >= startTime; i--) {
            currentTime = i - startTime;

            //Troubleshooting
            System.out.println(currentTime);

            Thread.sleep(1000);

            if (i == endTime) {
                channel.sendMessage(convertTimeToString(currentTime)).queue();
            }
            else if (i < endTime) {

                //Troubleshooting
                System.out.println("I've arrived here!");

                MessageHistory history = new MessageHistory(channel);
                List<Message> temp = history.retrievePast(10).complete();

                String functions = timerFunctions(author, channel, self, history, temp, currentTime);

                if (functions.equalsIgnoreCase("start")) {
                    return functions;
                }
                else if (functions.equalsIgnoreCase("stop")) {
                    return functions;
                }
            }
        }

        channel.sendMessage("Time's up!").queue();

        return "";
    }
    
    private String createTimer(User author, MessageChannel channel, User self, int currentTime) throws InterruptedException {
        
        int startTime = currentTime;
        
        for (int i = startTime; i >= 0; i--) {
            currentTime = startTime - (startTime - i);

            //Troubleshooting
            System.out.println(currentTime);

            Thread.sleep(1000);

            if (i == startTime) {
                channel.sendMessage(convertTimeToString(currentTime)).queue();
            }
            else if (i < startTime) {

                //Troubleshooting
//                System.out.println("I've arrived here!");

                MessageHistory history = new MessageHistory(channel);
                List<Message> temp = history.retrievePast(10).complete();

                String functions = timerFunctions(author, channel, self, history, temp, currentTime);

                if (functions.equalsIgnoreCase("start")) {
                    return functions;
                }
                else if (functions.equalsIgnoreCase("stop")) {
                    return functions;
                }
            }
        }

        channel.sendMessage("Time's up!").queue();

        return "";
    }
    
    private String timerFunctions(User author, MessageChannel channel, User self, MessageHistory history, List<Message> messages, int currentTime) {
        String id;

        for (int j = 0; j < messages.size(); j++) {
            boolean b = false;
            boolean pause = false;

            id = messages.get(j).getId();
            String messageRaw = history.getMessageById(id).toString();

            String message = messageRaw.substring(messageRaw.indexOf(":", 2) + 1, messageRaw.lastIndexOf("("));

            //Troubleshooting
//            System.out.println(message);

            if (!messages.get(j).getAuthor().getId().equalsIgnoreCase(self.getId())) {

                try {
                    createTimer(author, channel, self, currentTime);
                }
                catch (InterruptedException e) {
                    channel.sendMessage("There was a problem with creating the timer.").queue();
                }

                return "start";
            }

            if (messages.get(j).getAuthor().getId().equals(author.getId())) {
                if (message.equalsIgnoreCase("check time") || message.equalsIgnoreCase("get time")) {
                    channel.sendMessage(convertTimeToString(currentTime)).queue();
                }
                else if (message.equalsIgnoreCase("pause timer")) {
                    pause = true;

                    channel.sendMessage("The timer has been paused. Type \"start timer\" or \"unpause timer\" to start the timer again.").queue();
                }
                else if (message.equalsIgnoreCase("stop timer") ||message.equalsIgnoreCase("end timer")) {
                    channel.sendMessage("The timer has been ended.").queue();

                    return "stop";
                }

                b = true;
            }

            while (pause) {
                history = new MessageHistory(channel);
                messages = history.retrievePast(10).complete();

                id = messages.get(j).getId();
                messageRaw = history.getMessageById(id).toString();

                message = messageRaw.substring(messageRaw.indexOf(":", 2) + 1, messageRaw.lastIndexOf("("));

                for (int l = 0; l < messages.size(); l++) {
                    if (message.equalsIgnoreCase("start timer") || message.equalsIgnoreCase("unpause timer")) {

                        try {
                            createTimer(author, channel, self, currentTime);
                        }
                        catch (InterruptedException e) {
                            channel.sendMessage("There was a problem with creating the timer.").queue();
                        }

                        return "start";
                    }
                }
            }

            if (messages.get(j).getAuthor().getId().equals(self.getId())) {

                //Troubleshooting
//                System.out.println("Message Author ID: " + messages.get(j).getAuthor().getId());
//                System.out.println("Val ID: " + self.getId());
//                System.out.println("Message ID: " + id);
//                System.out.println("Message: " + message);

                channel.editMessageById(id, convertTimeToString(currentTime)).queue();

                b = true;
            }

            if (b) {
                break;
            }
        }

        return "";
    }
    
    private int getTotalTime(String[] times) {
        int time = 0;

        int multiplier = (int) Math.pow(60, (times.length - 1));

        for (String s : times) {
            time += (int) Math.round(Double.parseDouble(s)) * multiplier;
            multiplier /= 60;
        }

        return time;
    }

    private String convertTimeToString(int time) {
        String timeToString;

        if (time == 0) {
            return "00:00:00";
        }

        if (time % 3600 == time) {
            if (time % 60 == time) {
                 timeToString = "00:00:" + time;
            }
            else {
                String minutes = Double.toString( (double) time / 60);

                String seconds;

                if (minutes.contains(".")) {
                    String[] temp = minutes.split("[.]");

                    seconds = Double.toString(Math.round(Double.parseDouble("0." + temp[1]) * 60));

                    if (seconds.substring(0, 2).equals("60")) {
                        seconds = "00" + seconds.substring(2);
                        minutes = Double.toString(Double.parseDouble(minutes) + 1);
                    }

                    if (Double.parseDouble(minutes) < 10) {
                        minutes = "0" +  minutes;
                    }

                    if (Double.parseDouble(seconds) < 10) {
                        seconds = "0" + seconds;
                    }

                    timeToString = "00:" + minutes.substring(0, 2) + ":" + seconds.substring(0, 2);
                }
                else {
                    if (minutes.substring(0, minutes.indexOf(".")).length() == 1) {
                        timeToString =  "00:0" + minutes.substring(0, 1) + ":00";
                    }
                    else {
                        timeToString = "00:" + minutes.substring(0, 2) + ":00";
                    }
                }
            }

            return timeToString;
        }

        String hours = Double.toString((double) time / 3600);

        //Troubleshooting
//        System.out.println(hours);

        String minutes;
        String seconds;

        if (hours.contains(".")) {
            String[] temp1 = hours.split("[.]");

            //Troubleshooting
//            System.out.println(temp1);

            minutes = Double.toString(Double.parseDouble("0." + temp1[1]) * 60);

            if (minutes.substring(0, 2).equals("60")) {
                minutes = "00" + minutes.substring(2);
                hours = Double.toString(Double.parseDouble(hours) + 1);
            }

            if (Double.parseDouble(minutes) < 10) {
                minutes = "0" +  minutes;
            }

            if (minutes.contains(".")) {
                String[] temp2 = minutes.split("[.]");

                seconds = Double.toString(Math.round(Double.parseDouble("0." + temp2[1]) * 60));

                if (seconds.substring(0, 2).equals("60")) {
                    seconds = "00" + seconds.substring(2);
                    minutes = Double.toString(Double.parseDouble(minutes) + 1);
                }

                if (minutes.substring(0, 2).equals("60")) {
                    minutes = "00" + minutes.substring(2);
                    hours = Double.toString(Double.parseDouble(hours) + 1);
                }

                if (Double.parseDouble(minutes) < 10) {
                    minutes = "0" +  minutes;
                }

                if (Double.parseDouble(seconds) < 10) {
                    seconds = "0" + seconds;
                }

                timeToString = hours.substring(0, hours.indexOf(".")) + ":" + minutes.substring(0, 2) + ":" + seconds.substring(0, 2);

                if (timeToString.length() < 8) {
                    timeToString = timeToString.substring(0, timeToString.lastIndexOf(":")) + ":0" + timeToString.substring(timeToString.length() - 1);
                }
            }
            else {
                if (minutes.substring(0, minutes.indexOf(".")).length() == 1) {
                    timeToString = hours.substring(0, hours.indexOf(".")) + ":0" + minutes.substring(0, 2) + ":00";
                }
                else {
                    timeToString = hours.substring(0, hours.indexOf(".")) + ":" + minutes.substring(0, 2) + ":00";
                }
            }
        }
        else {
            if (hours.substring(0, hours.indexOf(".")).length() == 1) {
                timeToString = "0" + hours.substring(0, hours.indexOf(".")) + ":00:00";
            }
            else {
                timeToString = hours.substring(0, hours.indexOf(".")) + ":00:00";
            }
        }

        return timeToString;
    }
}