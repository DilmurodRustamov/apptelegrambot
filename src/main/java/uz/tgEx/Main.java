package uz.tgEx;

import com.google.gson.Gson;
import lombok.SneakyThrows;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main extends TelegramLongPollingBot {
    private static int status = 0;

    public static void main(String[] args) throws TelegramApiRequestException {
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        telegramBotsApi.registerBot(new Main());

    }

    @SneakyThrows
    public void onUpdateReceived(Update update) {
        String text = update.getMessage().getText();
        System.out.println(text);
        Long chatId = update.getMessage().getChatId();
        SendMessage sendMessage = new SendMessage();
        if (text.toLowerCase().equals("/start")) {
            sendMessage.setChatId(chatId);
            sendMessage.setText("Valyuta turini tanlang");
            //-------
            ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
            replyKeyboardMarkup.setResizeKeyboard(true).setSelective(true);
            List<KeyboardRow> keyboardRowList = new ArrayList<KeyboardRow>();
            KeyboardRow keyboardRow1 = new KeyboardRow();
            KeyboardButton keyboardButtonDollar = new KeyboardButton();
            KeyboardButton keyboardButtonSom = new KeyboardButton();
            keyboardButtonDollar.setText("dollar ==> so'm");
            keyboardButtonSom.setText("so'm ==> dollar");
            keyboardRow1.add(keyboardButtonDollar);
            keyboardRow1.add(keyboardButtonSom);
            keyboardRowList.add(keyboardRow1);
            replyKeyboardMarkup.setKeyboard(keyboardRowList);
            sendMessage.setReplyMarkup(replyKeyboardMarkup);


            //----------------

        } else if (text.toLowerCase().equals("dollar ==> so'm")) {
            sendMessage.setChatId(chatId);
            sendMessage.setText("Qiymatni kiriting");
            status = 10;
        } else if (status == 11 && Double.parseDouble(text) > 0) {
            Course[] course = getCourse();
            double amount = Double.parseDouble(text);
            sendMessage.setChatId(chatId);
            double dollar = amount * Double.parseDouble(course[0].getRate());
            sendMessage.setText(String.valueOf(amount + "$ ==> " + dollar + " UZS ga teng"));
        } else if (text.toLowerCase().equals("so'm ==> dollar")) {
            sendMessage.setChatId(chatId);
            sendMessage.setText("Qiymatni kiriting");
            status = 20;
        } else if (status == 21 && Double.parseDouble(text) > 0) {
            Course[] course = getCourse();
            double amount2 = Double.parseDouble(text);
            sendMessage.setChatId(chatId);
            double som = (amount2 / Double.parseDouble(course[0].getRate()));
            sendMessage.setText(String.valueOf(amount2 + " SO'M ==> " + som + " Dollar ga teng"));
        }
        try {
            execute(sendMessage);
            status++;
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public String getBotUsername() {
        return "currencyDollarKursBot";
    }

    public String getBotToken() {
        return "1584026629:AAE7ZJEzYU8HVqdLu-Z0xAAzBES2t96pYAQ";
    }

    public static Course[] getCourse() throws IOException {
        HttpClient client = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("http://cbu.uz/ru/arkhiv-kursov-valyut/json/");
        HttpResponse response = client.execute(httpGet);
        InputStream inputStream = response.getEntity().getContent();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line = reader.readLine();
        String javob = "";
        while (line != null) {
            javob += line;
            line = reader.readLine();
        }

        Gson gson = new Gson();
        return gson.fromJson(javob, Course[].class);

//        2-usul
//        Course[] courses = gson.fromJson(javob, Course[].class);
//        return courses;

    }
}