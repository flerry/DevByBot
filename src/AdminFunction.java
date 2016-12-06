
class AdminFunction {
    public static void alertUsers(String alert) {
        TgBot msgBridge = new TgBot();
        for (Long i : NewsSubscribe.subscribeIDUser) {
            msgBridge.sendMsgCustomUser(i, alert);
        }
    }

    public static void getIdUsers() {
        TgBot msgBridge = new TgBot();
        for (Long i : NewsSubscribe.subscribeIDUser) {
            msgBridge.sendMsgCustomUser(TgBot.chatId, i.toString());
        }
    }
}
