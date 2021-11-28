package com.gmail.arkgaranin.tests;


import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import net.lingala.zip4j.exception.ZipException;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static com.gmail.arkgaranin.filesHelper.Files.readTextFromPath;
import static com.gmail.arkgaranin.filesHelper.Zip.unzip;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class SelenideFilesTests extends TestBase {

  @Test
  void uploadFileTest() {
    open("https://tympanus.net/Tutorials/CustomFileInputs/");

    $(".content > .box:nth-child(1) > input[type=\"file\"]")
        .uploadFromClasspath("pdf_converter.pdf");

    $(".content > .box:nth-child(1) > [for=file-1] > span").shouldHave(text("pdf_converter.pdf"));
  }

  @Test
  void verifyTxtFileTest() throws Exception {
    String textResult;
    try (InputStream stream = getClass().getClassLoader().getResourceAsStream("TextFile.txt")) {
      textResult = new String(Objects.requireNonNull(stream).readAllBytes(), StandardCharsets.UTF_8);
    }
    assertThat(textResult).contains("Таким образом дальнейшее развитие различных форм деятельности играет");
  }

  @Test
  void downloadAndVerifyPdfFileTest() throws Exception {
    open("https://docs.arenadata.io/adh/download_pdf.html");

    File download = $(".document").$(byText("Инструкция по установке кластера ADH")).download();
    PDF parsed = new PDF(download);
    assertThat(parsed.numberOfPages).isEqualTo(38);
  }

  @Test
  void verifyPdfFileTest() throws Exception {
    try (InputStream stream = getClass().getClassLoader().getResourceAsStream("pdf_converter.pdf")) {
      PDF parsed = new PDF(stream);
      assertThat(parsed.title).contains("Краткое справочное руководство");
      assertThat(parsed.author).contains("Nuance Communications, Inc.");
      assertThat(parsed.numberOfPages).isEqualTo(59);
    }
  }

  @Test
  void verifyExcelFileTest() throws Exception {
    try (InputStream stream = getClass().getClassLoader().getResourceAsStream("butsort.xlsx")) {
      XLS parsed = new XLS(stream);
      assertThat(parsed.excel.getSheetAt(0).getRow(3).getCell(0).getStringCellValue())
          .isEqualTo("Товар 3");
    }
  }

  @Test
  void verifyZipFileWithPasswordTest() throws IOException, ZipException {
    String zipFilePath = "./src/test/resources/zip_with_password.zip";
    String unzipFolderPath = "./src/test/resources/unzip";
    String zipPassword = "qaguru123";
    String unzipTxtFilePath = "./src/test/resources/unzip/File_in_zip.txt";
    String expectedData = "Hello World!";

    unzip(zipFilePath, unzipFolderPath, zipPassword);

    String actualData = readTextFromPath(unzipTxtFilePath);

    assertThat(actualData).contains(expectedData);
  }

  @Test
  void verifyDocxFileTest() throws Exception {
    try (InputStream stream = getClass().getClassLoader().getResourceAsStream("Test (beginner elementary).docx"))
    {

      StringBuilder fullText = new StringBuilder();

      XWPFDocument document = new XWPFDocument(stream);

      List<XWPFParagraph> paragraphs = document.getParagraphs();

      for (XWPFParagraph paragraph : paragraphs) {
        fullText.append(paragraph.getText());
      }
      assertThat(fullText.toString()).contains("Раскройте скобки (поставьте глагол в нужное время)");
    }
  }
}