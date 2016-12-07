import org.telegram.telegrambots.TelegramApiException;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendChatAction;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


class TgBot extends TelegramLongPollingBot {
    static long chatId;
    private static final String NAME = "byDevBot";
    private static final String TOKEN = "286707737:AAFg9W59KppkqWHxdlAGG3PxbNPG9VDv14U";
    private static final String TXT_PATH = "C:/Users/nurye/IdeaProjects/DevByBot/TelegramUserID.txt"; //server path  /home/Flerry/TestDevByBot/TelegramUserID.txt    pc path  C:/Users/nurye/IdeaProjects/DevByBot/TelegramUserID.txt
    private static final String IMG_PATH = "C:/Users/nurye/IdeaProjects/DevByBot/2016-12-05_18-38-22.png";
    private static final String HELLO_MSG = "! Я - бот ресурса Dev.by и я всегда помогаю получить актуальную информацию с нашего сайта! Воспользуйтесь кнопками...";
    private static final String UNSUBSCRIBE = ", Ваша подписка успешно удалена!\nЧтобы оформить подписку снова, нажмите на \"subscribe\"";
    private static final String SUBSCRIBE = ", Ваша подписка успешно оформлена!\nЧтобы удалить подписку, нажмите на \"subscribe\"";
    private static final String ACTION_TYPING = "typing";
    private static final String ACTION_PHOTO = "upload_photo";
    private static final String CONTACTS = "[VK] - https://vk.com/devby\n[FACEBOOK] - https://www.facebook.com/devbyby\n[TWITTER] - https://twitter.com/devby\n[\uD83D\uDCE7] - dev@dev.by";
    private static final String FEEDBACK_MSG = "Если Вам понравился наш бот, поставьте ему" + "\uD83C\uDF1F" + "\uD83C\uDF1F" + "\uD83C\uDF1F" + "\uD83C\uDF1F" + "\uD83C\uDF1F" + "здесь, пожалуйста:\n" + "https://storebot.me/bot/bydevbot";
    private static final HashMap <Long, Integer> checkSpam = new HashMap();

    public static void main(String[] args) {
        System.out.println("***Start service***");
        try {
            LineNumberReader reader = new LineNumberReader(new FileReader(TXT_PATH));

            String line;
            while ((line = reader.readLine()) != null) {
                NewsSubscribe.subscribeIDUser.add(Long.valueOf(line));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("LIST ID" + " " + NewsSubscribe.subscribeIDUser);


        NewsSubscribe newsObject = new NewsSubscribe();
        newsObject.start();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            telegramBotsApi.registerBot(new TgBot());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return NAME;
    }

    @Override
    public String getBotToken() {
        return TOKEN;
    }

    @Override
    public void onUpdateReceived(Update update) {  //any commands and instructions for this commands
        Message message = update.getMessage();
        if (message != null && message.hasText()) {
            chatId = message.getChatId();
            if (checkSpam.containsKey(message.getChatId())) {
                if (message.getDate() - checkSpam.get(message.getChatId()) < 5) {
                    sendMsg(message, "Пожалуйста, не посылайте сообщения так часто!");
                    message = null;

                } else if (message.getDate() - checkSpam.get(message.getChatId()) > 5) {
                    checkSpam.put(message.getChatId(), message.getDate());
                }
            } else {
                checkSpam.put(message.getChatId(), message.getDate());
            }
            try {
                String msgInfo = "[" + message.getDate() + "]" + " " + "[" + message.getFrom().getFirstName() + " " + message.getFrom().getLastName() + "]" + " " + "id" + " " + message.getFrom().getId() + " ";
                if (message.getText().equals("/start")) {
                    sendMsg(message, "Здравствуйте, " + message.getFrom().getFirstName() + HELLO_MSG);
                }
                if (message.getText().equals("subscribe" + "\uD83D\uDCF0")) {
                    if (NewsSubscribe.subscribeIDUser.contains(message.getChatId())) {
                        NewsSubscribe.subscribeIDUser.remove(message.getChatId());
                        StringBuilder set = new StringBuilder();
                        for (Long i : NewsSubscribe.subscribeIDUser) {
                            set.append(i).append("\n");
                        }
                        FileWork.writeFile(set.toString());
                        sendMsg(message, message.getFrom().getFirstName() + UNSUBSCRIBE);
                        System.out.println(msgInfo + "Unsubscribed");
                    } else {
                        NewsSubscribe.subscribeIDUser.add(message.getChatId());
                        sendMsg(message, message.getFrom().getFirstName() + SUBSCRIBE);
                        System.out.println(msgInfo + "Subscribe");
                        FileWork.update(message.getChatId().toString() + "\r\n");
                    }
                }
                if (message.getText().equals("events" + "\u2B50")) {
                    sendChatAction(ACTION_TYPING);
                    ParseEvent thread = new ParseEvent();
                    thread.start();
                    if (!thread.isInterrupted()) {
                        thread.interrupt();
                    }
                    System.out.println(msgInfo + "Events");

                }
                if (message.getText().contains("jobs")) {
                    sendMsg(message, ParseJobs.parseJobs(message.getText().replaceAll("/jobs ", "")).toString());
                    System.out.println(msgInfo + "Jobs" + " " + message.getText().replaceAll("/jobs ", ""));
                }

                if (message.getText().equals("salaries" + "\uD83D\uDCB8")) {
                    sendChatAction(ACTION_PHOTO);
                    ParseSalaries thread = new ParseSalaries();
                    thread.start();
                    if (!thread.isInterrupted()) {
                        thread.interrupt();
                    }
                    sendImageUploadingAFile(message.getChatId().toString());
                    System.out.println(msgInfo + "Salaries");
                }
                if (message.getText().contains("feedback" + "\u2764")) {
                    sendChatAction(ACTION_TYPING);
                    sendMsg(message, FEEDBACK_MSG);
                    sendMsgCustomUser(203110206, message.getText().replaceAll("feedback", ""));
                    System.out.println(msgInfo + "Feedback");
                }
                if (message.getText().equals("community" + "\uD83D\uDCF1")) {
                    sendChatAction(ACTION_TYPING);
                    sendMsg(message, CONTACTS);
                    System.out.println(msgInfo + "Comunity");
                }
                if (message.getText().contains("more" + "\u2753")) {
                    sendChatAction(ACTION_TYPING);
                    System.out.println(msgInfo + "More");
                    sendMsg(message, "jobs <Название вакансии или язык программирования>");
                }
                if (message.getText().contains("alertUsers")) {
                    AdminFunction.alertUsers(message.getText().replaceAll("alertUsers ", ""));
                    System.out.println(msgInfo + "Alert");
                }
                if (message.getText().equals("getIdUsers")) {
                    AdminFunction.getIdUsers();
                    System.out.println(msgInfo + "GetIdUsers");
                }
            } catch (NullPointerException ignored) {
            }
        }
    }


    private void sendMsg(Message message, String text) {  //a method for sending messages in response to the command
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(); //make a custom keyboard
        sendMessage.setReplyMarkup(replyKeyboardMarkup);

        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboad(false);

        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow keyboardFirstRow = new KeyboardRow();
        keyboardFirstRow.add("subscribe" + "\uD83D\uDCF0");

        KeyboardRow keyboardSecondRow = new KeyboardRow();
        keyboardSecondRow.add("salaries" + "\uD83D\uDCB8");
        keyboardSecondRow.add("events" + "\u2B50");
        keyboardSecondRow.add("community" + "\uD83D\uDCF1");

        KeyboardRow keyboardThreeRow = new KeyboardRow();
        keyboardThreeRow.add("feedback" + "\u2764");
        keyboardThreeRow.add("more" + "\u2753");

        keyboard.add(keyboardFirstRow);
        keyboard.add(keyboardSecondRow);
        keyboard.add(keyboardThreeRow);

        replyKeyboardMarkup.setKeyboard(keyboard);

        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setReplyToMessageId(message.getMessageId());
        sendMessage.setText(text);
        try {
            sendMessage(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    void sendMsgCustomUser(long chatId, String text) { //a method for sending messages to any user
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(text);
        try {
            sendMessage(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendImageUploadingAFile(String chatId) {
        SendPhoto sendPhotoRequest = new SendPhoto();
        sendPhotoRequest.setChatId(chatId);
        sendPhotoRequest.setNewPhoto(new File(IMG_PATH));
        try {
            sendPhoto(sendPhotoRequest);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendChatAction(String action) {
        SendChatAction sendChatActionRequest = new SendChatAction();
        sendChatActionRequest.setChatId(String.valueOf(chatId));
        sendChatActionRequest.setAction(action);
        try {
            sendChatAction(sendChatActionRequest);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}