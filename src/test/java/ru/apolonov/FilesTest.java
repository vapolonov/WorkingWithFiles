package ru.apolonov;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.*;

public class FilesTest {

    @Test
    @DisplayName("Загрузка файла по абсолютному пути (не рекомендуется)")
    void fileNameShouldDisplayedAfterUploadActionAbsolutePathTest() {
        open("https://the-internet.herokuapp.com/upload");
        File exampleFile = new File("D:\\vasvap\\QA Engineer\\Java\\Projects\\WorkingWithFiles\\src\\test\\resources\\example.txt");
        $("input[type='file']").uploadFile(exampleFile);
        $("#file-submit").click();
        $("#uploaded-files").shouldHave(text("example.txt"));
    }

    @Test
    @DisplayName("Загрузка файла по относительному пути (рекомендуется)")
    void fileNameShouldDisplayedAfterUploadActionFromClassPathTest() {
        open("https://the-internet.herokuapp.com/upload");
        $("input[type='file']").uploadFromClasspath("example.txt");
        $("#file-submit").click();
        $("#uploaded-files").shouldHave(text("example.txt"));
    }

    @Test
    @DisplayName("Скачивание текстового файла и проверка его содержимого")
    void downloadSimpleTextFileTest() throws IOException {
        open("https://github.com/junit-team/junit5/blob/main/README.md");
        File download = $("#raw-url").download();
        String fileContent = IOUtils.toString(new FileReader(download));
        Assertions.assertTrue((fileContent.contains("This repository is the home of the next generation of JUnit, _JUnit 5_.")));
        //Если элемент загрухкт файла (кнопка) не содержит аттрибут href
        //то можно использовать другой способ: selenide запустит настоящий прокси-сервер между нашим кодом и браузером,
        // который будет пытаться отловить трафик и вычислить ссылку с href
        //не рекомендуется делать т.к. тесты становяться нестабильными
        /*
        Configuration.proxyEnabled = true;
        Configuration.fileDownload = FileDownloadMode.PROXY;
        */
    }

    @Test
    @DisplayName("Скачивание pfd-файла")
    void pdfFileDownloadTest() throws FileNotFoundException {
        open("https://junit.org/junit5/docs/current/user-guide/");
        File pdf = $(By.linkText("PDF download")).download();


    }

}
