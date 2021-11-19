package ru.apolonov;

import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        assertTrue((fileContent.contains("This repository is the home of the next generation of JUnit, _JUnit 5_.")));
        //Если элемент загрузки файла (кнопка) не содержит аттрибут href
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
    void pdfFileDownloadTest() throws IOException {
        open("https://junit.org/junit5/docs/current/user-guide/");
        File pdf = $(byText("PDF download")).download();
        PDF parsedPdf = new PDF(pdf);
        Assertions.assertEquals(164, parsedPdf.numberOfPages);
    }

    @Test
    @DisplayName("Скачивание xls-файла")
    void xlsFileDownloadTest() throws IOException {
        open("http://romashka2008.ru/");
        File xls = $$("a[href*='prajs']").find(text("Скачать Прайс-лист Excel")).download();
        XLS parsedXls = new XLS(xls);
        boolean checkPassed = parsedXls.excel.getSheetAt(0).getRow(10).getCell(1).getStringCellValue().contains("ООО \"Ромашка\"");
        assertTrue(checkPassed);
    }

    @Test
    @DisplayName("Парсинг CSV файлов")
    void parseCsvFileTest() throws IOException, CsvException {
        ClassLoader classloader = this.getClass().getClassLoader();
        try (InputStream is = classloader.getResourceAsStream("csv.csv");
             Reader reader = new InputStreamReader(is)) {
            CSVReader csvReader = new CSVReader(reader);
            List<String[]> strings = csvReader.readAll();
            assertEquals(strings.size(), 3);
        }
    }

    @Test
    @DisplayName("Парсинг ZIP файлов")
    void parseZipFileTest() throws IOException {
        ClassLoader classloader = this.getClass().getClassLoader();
        try (InputStream is = classloader.getResourceAsStream("zip_2MB.zip");
             ZipInputStream zis = new ZipInputStream(is)) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                System.out.println(entry.getName());
            }
        }
    }
}

