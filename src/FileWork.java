import java.io.*;

class FileWork {
    private static final String TXT_PATH = "/home/Flerry/TestDevByBot/TelegramUserID.txt";

    //work with files
    public static void writeFile(String text) {
        File file = new File(TXT_PATH);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            try (PrintWriter out = new PrintWriter(file.getAbsoluteFile())) {
                out.print(text);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String read() {
        File file = new File(TXT_PATH);
        StringBuilder sb = new StringBuilder();
        try {
            try (BufferedReader in = new BufferedReader(new FileReader(file.getAbsoluteFile()))) {
                String s;
                while ((s = in.readLine()) != null) {
                    sb.append(s);
                    sb.append("\r\n");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return sb.toString();
    }


    public static void update(String newText) {
        StringBuilder sb = new StringBuilder();
        String oldFile = read();
        sb.append(oldFile);
        sb.append(newText);
        writeFile(sb.toString());
    }
}
