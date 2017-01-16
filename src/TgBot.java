import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.telegram.telegrambots.TelegramApiException;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendChatAction;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


class TgBot extends TelegramLongPollingBot {
    static long chatId;

    private static final String NAME = "";
    private static final String TOKEN = "";
    private static final String TXT_PATH = "";
    private static final String HELLO_MSG = "! Я - бот ресурса Dev.by. Получайте ИТ-новости одновременно с их выходом на сайте и пользуйтесь дополнительными возможностями с помощью кнопок.";
    private static final String HELP_MSG = "Subscribe\uD83D\uDCF0:\n- Подписаться на новости dev.by (или отписаться от них)\n\nLast news\uD83C\uDD95\n- Получить последнюю новость\n\nJobs\uD83D\uDCE3\n- Актуальные ИТ-вакансии\n\n" +
            "Events\u2B50\n- Список ИТ-событий ближайшей недели\n\nCommunity\uD83D\uDCF1\n- Сообщества dev.by\n\nFeedback\u2764\n- Оставить отзыв\n\nMore\u00AE\n- Иные команды";
    private static final String UNSUBSCRIBE = ", *Ваша подписка успешно удалена*!\nЧтобы оформить подписку снова, нажмите на \"subscribe\"";
    private static final String SUBSCRIBE = ", *Ваша подписка успешно оформлена*!\nЧтобы удалить подписку, нажмите на \"subscribe\"";
    private static final String ACTION_TYPING = "typing";
    private static final String CONTACTS = "*SLACK* - https://devby.slack.com\n*VK* - https://vk.com/devby\n*FACEBOOK* - https://www.facebook.com/devbyby\n*TWITTER* - https://twitter.com/devby\n[\uD83D\uDCE7] - dev@dev.by";
    private static final String FEEDBACK_MSG = "Если вам всё нравится, оставьте отзыв здесь:\n" + "https://storebot.me/bot/bydevbot\n" + "Если не нравится, напишите, в чём именно проблема – мы подумаем ;)";
    private static final HashMap<Long, Integer> checkSpam = new HashMap<Long, Integer>();

    public static void main(String[] args) {  //main method
        System.out.println("***Start service***");
        try {
            LineNumberReader reader = new LineNumberReader(new FileReader(TXT_PATH));

            String line;
            while ((line = reader.readLine()) != null) {  //add subscribe chatID in List
                NewsSubscribe.subscribeIDUser.add(Long.valueOf(line));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("LIST ID" + " " + NewsSubscribe.subscribeIDUser);


        NewsSubscribe newsObject = new NewsSubscribe();
        newsObject.start();  //service subscribe start
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
                if (message.getDate() - checkSpam.get(message.getChatId()) < 3) {
                    sendMsg(message, "Пожалуйста, не посылайте сообщения так часто!");
                    message = null;

                } else if (message.getDate() - checkSpam.get(message.getChatId()) > 3) {
                    checkSpam.put(message.getChatId(), message.getDate());
                }
            } else {
                checkSpam.put(message.getChatId(), message.getDate());
            }
            try {
                String msgInfo = "[" + message.getDate() + "]" + " " + "[" + message.getFrom().getFirstName() + " " + message.getFrom().getLastName() + "]" + " " + "id" + " " + message.getFrom().getId() + " ";
                if (message.getText().equals("text")) {
                    String url = "https://dev.by/lenta";
                    Document mainPage;
                    try {
                        mainPage = Jsoup.connect(url).timeout(30000).get();

                        Element itemRateLinear = mainPage.select("div[class=item-rate_linear]").first();

                        Elements itemRateLinks = itemRateLinear.select("a[href]");

                        String articleLink = itemRateLinks.attr("abs:href");

                        Document linkToText = Jsoup.connect(articleLink).timeout(30000).get();

                        String text = linkToText.select("div[class=text js-mediator-article]").text();

                        sendMsg(message, text.substring(0, 2000));

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }


                if (message.getText().equals("/start")) {
                    sendMsg(message, "Привет, " + message.getFrom().getFirstName() + HELLO_MSG);
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
                if (message.getText().equals("last news" + "\uD83C\uDD95")) {
                    sendChatAction(ACTION_TYPING);
                    GetLastNews.getLastNews();
                }
                if (message.getText().equals("events" + "\u2B50")) {
                    sendChatAction(ACTION_TYPING);
                    ParseEvent thread = new ParseEvent();
                    thread.start();
                    System.out.println(msgInfo + "Events");

                }
                if (message.getText().equals("jobs" + "\uD83D\uDCE3")) {
                    sendMsg(message, "Введите:\n*jobs* _название вакансии или язык программирования_\n\nПример:\n*jobs java*");
                }

                if (message.getText().contains("jobs") && !message.getText().contains("\uD83D\uDCE3")) {
                    sendMsg(message, ParseJobs.parseJobs(message.getText().replaceAll("jobs ", "")).toString());
                    System.out.println(msgInfo + "Jobs" + " " + message.getText().replaceAll("/jobs ", ""));
                }

                if (message.getText().equals("salaries")) {
                    sendChatAction(ACTION_TYPING);
                    ParseSalaries thread = new ParseSalaries();
                    thread.start();
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
                if (message.getText().equals("help" + "\u2753")) {
                    sendChatAction(ACTION_TYPING);
                    sendMsg(message, HELP_MSG);
                }
                if (message.getText().contains("more" + "\u00AE")) {
                    sendChatAction(ACTION_TYPING);
                    System.out.println(msgInfo + "More");
                    sendMsg(message, "*salaries* - актуальная зарплата");
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
        keyboardSecondRow.add("jobs" + "\uD83D\uDCE3");
        keyboardSecondRow.add("last news" + "\uD83C\uDD95");
        keyboardSecondRow.add("events" + "\u2B50");


        KeyboardRow keyboardThreeRow = new KeyboardRow();
        keyboardThreeRow.add("feedback" + "\u2764");
        keyboardThreeRow.add("community" + "\uD83D\uDCF1");
        keyboardThreeRow.add("more" + "\u00AE");

        KeyboardRow keyboardFourthRow = new KeyboardRow();
        keyboardFourthRow.add("help" + "\u2753");


        keyboard.add(keyboardFirstRow);
        keyboard.add(keyboardSecondRow);
        keyboard.add(keyboardThreeRow);
        keyboard.add(keyboardFourthRow);

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
