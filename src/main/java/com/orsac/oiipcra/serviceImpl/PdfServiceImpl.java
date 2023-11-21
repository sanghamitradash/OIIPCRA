package com.orsac.oiipcra.serviceImpl;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.draw.LineSeparator;
import com.lowagie.text.pdf.draw.VerticalPositionMark;
import com.orsac.oiipcra.bindings.OIIPCRAResponse;

import com.orsac.oiipcra.bindings.TenderInfo;
import com.orsac.oiipcra.dto.*;
import com.orsac.oiipcra.entities.*;
import com.orsac.oiipcra.repository.DashboardQryRepository;
import com.orsac.oiipcra.repository.TenderNoticePublishRepository;
import com.orsac.oiipcra.repository.UserQueryRepository;
import com.orsac.oiipcra.repositoryImpl.OfficeDataRepositoryImpl;
import com.orsac.oiipcra.repositoryImpl.TenderRepositoryImpl;
import com.orsac.oiipcra.service.DashboardService;
import com.orsac.oiipcra.service.OfficeDataService;
import com.orsac.oiipcra.service.PdfService;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.xhtmlrenderer.pdf.ITextRenderer;

import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.*;
import java.math.BigDecimal;
import java.text.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;


@Service
@Slf4j
public class PdfServiceImpl implements PdfService {
    private static String path = "/Users/bhagyashreepalai/bhagya_dev/OIIPCRA/print.txt";

    @Autowired
    TenderRepositoryImpl tenderRepositoryImpl;
    @Autowired
    private TenderNoticePublishRepository tenderNoticePublishRepository;
    @Autowired
    UserQueryRepository userQueryRepository;
    @Autowired
    OfficeDataRepositoryImpl officeDataRepositoryImpl;

    @Autowired
    private DashboardQryRepository dashboardQryRepository;

    @Autowired
    OfficeDataService officeDataService;

    @Autowired
    private DashboardService dashboardService;

    private static final String PDF_RESOURCES = "/pdf-resources/";
    //  @Autowired
//  private StudentService studentService;
//  @Autowired
//  private StudentRepository studentRepository;
    @Autowired
    private SpringTemplateEngine templateEngine;

  /*@Value("${octdmsLogoPath}")
  private String octdmsLogoPath;*/

  /*@Value("${odishaWhiteLogoPath}")
  private String odishaWhiteLogoPath*/

//  private PdfServiceRepositoryImpl pdfServiceRepository;


    private void writeTableHeader(PdfPTable pdfPTable, Integer tenderId) {
        DraftTenderNoticeDto draftTenderNotice = tenderRepositoryImpl.getAllTenderNotice(tenderId);

        PdfPCell cell = new PdfPCell();
        cell.setPadding(5);

        Font font3 = FontFactory.getFont(FontFactory.HELVETICA);
        font3.setColor(Color.BLACK);

/*
    cell.setPhrase(new Phrase("Name Of Work", font3));
    pdfPTable.addCell(cell);
    cell.setPhrase(new Phrase("Tank ID", font3));
    pdfPTable.addCell(cell);*/
    }

    private void writeTableData(PdfPTable pdfTable, Integer tenderId) {
        DraftTenderNoticeDto draftTenderNotice = tenderRepositoryImpl.getAllTenderNotice(tenderId);
        Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        fontTitle.setSize(11);

        pdfTable.addCell("Name of Work:");
        pdfTable.addCell(draftTenderNotice.getNameOfWork());

        pdfTable.addCell("Total No. Works:");
        pdfTable.addCell(draftTenderNotice.getNoOfWorks() + " Nos");

        pdfTable.addCell("Estimated Cost Of Works:");
        pdfTable.addCell("Varies Approximately From Rs " + draftTenderNotice.getVariesFrom() + " Lakhs to Rs  " + draftTenderNotice.getVariesTo() + " Lakhs.(Total estimated cost of the " + draftTenderNotice.getNoOfWorks() + " Nos of works Rs " + draftTenderNotice.getTenderAmount() + " Lakh");

        pdfTable.addCell("Cost Of Bid Document:");
        pdfTable.addCell("Free of cost");

        pdfTable.addCell("Time For Completion:");
        pdfTable.addCell("Refer to DTCN");

        pdfTable.addCell("Availability of bid Documents:");
        pdfTable.addCell("From  " + draftTenderNotice.getAvailabilityDocumentFrom() + " to " + draftTenderNotice.getAvailabilityDocumentTo() + " through online");

        pdfTable.addCell("Date of Pre-Bid Meeting:");
        pdfTable.addCell("On dt : " + draftTenderNotice.getPreBidMeetingDate());

        pdfTable.addCell("Period for receipt of bid(s):");
        pdfTable.addCell("From " + draftTenderNotice.getReceiptBidFrom() + " to " + draftTenderNotice.getReceiptBidTo() + " online");

        pdfTable.addCell("Payment of bid cost/bid security:");
        pdfTable.addCell("Shall be paid On-line through the E-Procurement portal of Govt.of Odisha.");

        pdfTable.addCell("Date of opening of bid(s):");
        pdfTable.addCell("On dt : " + draftTenderNotice.getTenderOpeningDate() + " onwards.");

        pdfTable.addCell("Details about the tender:");
        pdfTable.addCell("Available in website www.dowrodisha.gov.in and www.tendersodisha.gov.in");

        pdfTable.addCell("Name/Address of the OIT:");
        pdfTable.addCell("Project Director, OCTDMS, Odisha. Address : 7th Floor Rajiv Bhawan,Bhubaneswar-751001" + System.lineSeparator() + "ph.0674-2512421,E-Mail:spuoiipcra.od@gov.in");

    }
/*
  public void draftTenderNotice(HttpServletResponse response, Integer tenderId, Integer issueNo) throws IOException, DocumentException {
    Document document = new Document(PageSize.A4);
    PdfWriter.getInstance(document, response.getOutputStream());
    DraftTenderNoticeDto draftTenderNotice = tenderRepositoryImpl.getAllTenderNotice(tenderId);
    document.open();
    Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
    fontTitle.setSize(11);
    com.lowagie.text.Font font = FontFactory.getFont(FontFactory.HELVETICA);
    font.setColor(Color.BLACK);
    font.isBold();
    document.open();
    Image image = Image.getInstance("/Users/bhagyashreepalai/IdeaProjects/OIIPCRA-Back-End-GIT/src/main/resources/img/octdms-logo.png");
    image.scaleAbsolute(70f, 70f);
    image.setAbsolutePosition(30, 720);
    Image image1 = Image.getInstance("/Users/bhagyashreepalai/IdeaProjects/OIIPCRA-Back-End-GIT/src/main/resources/img/odishaWhite.png");
    image1.scaleAbsolute(70f, 70f);
    image1.setAbsolutePosition(500, 720);
    Paragraph paragraph = new Paragraph("GOVERNMENT OF ODISHA", fontTitle);
    paragraph.setAlignment(Paragraph.ALIGN_CENTER);
    String para1 = "Odisha Community Tank Development and Management Society" + System.lineSeparator() +
            "7th Floor Rajiv Bhawan,Bhubaneswar-751001" + System.lineSeparator() + "ph.0674-2512421,E-Mail:spuoiipcra.od@gov.in" + System.lineSeparator() + "";
    Paragraph paragraph1 = new Paragraph(para1, font);
    //LineSeparator UNDERLINE = new LineSeparator(1f, 90, Color.BLACK, Element.ALIGN_CENTER, -10);
    paragraph1.setAlignment(Paragraph.ALIGN_CENTER);
    paragraph1.add(image);
    paragraph1.add(image1);
    // paragraph1.setSpacingBefore();
    String para2 = "ODISHA INTEGRATED IRRIGATION PROJECT FOR CUMATERESILIENT AGRICULTURE(OIIPCRA)" + System.lineSeparator() + "(WORLD BANK PROJECT ID:P163533)" +
            System.lineSeparator() + "REQUEST FOR BIDS(RFB) E-Procurement Notice";
    Paragraph paragraph4 = new Paragraph(para2, fontTitle);
    paragraph4.setAlignment(Paragraph.ALIGN_CENTER);
    paragraph4.setSpacingBefore(15);
    String headerProjectInfo = "NATIONAL OPEN COMPETITIVE PROCUREMENT FOR SMALL WORKS";
    Paragraph projectInfo = new Paragraph(headerProjectInfo, fontTitle);
    projectInfo.setAlignment(Paragraph.ALIGN_CENTER);
    projectInfo.setSpacingBefore(10);
    LineSeparator UNDERLINE = new LineSeparator(1f, 80, Color.BLACK, Element.ALIGN_CENTER, -10);
    String para3 = "               RFB No: ";
    Paragraph paragraph5 = new Paragraph(para3, fontTitle);
    paragraph5.setAlignment(Paragraph.ALIGN_LEFT);
    paragraph5.setSpacingBefore(10);
    String para5 = "               RFB No: ";
    Paragraph paragraph7 = new Paragraph(para5, fontTitle);
    paragraph7.setAlignment(Paragraph.ALIGN_RIGHT);
    paragraph7.setSpacingBefore(10);
    Chunk glue7 = new Chunk(new VerticalPositionMark());
    Paragraph p7 = new Paragraph("                       RFB No:" + draftTenderNotice.getBidId());
    p7.add(new Chunk(glue7));
    p7.add("Date:                           " + draftTenderNotice.getIssueDate());
    p7.setSpacingBefore(15);
    LineSeparator UNDERLINE2 = new LineSeparator(1f, 80, Color.BLACK, Element.ALIGN_CENTER, -10);
    Chunk glue5 = new Chunk(new VerticalPositionMark());
    Paragraph p5 = new Paragraph("MemoNo.  Date:");
    p5.add(new Chunk(glue5));
    p5.add("");
    p5.setSpacingBefore(15);
    Font fontParagraph = FontFactory.getFont(FontFactory.HELVETICA);
    fontParagraph.setSize(12);
    String footer = " Copy in duplicate along with soft copy of the tender notice submitted to the Deputy Director (Advertisement)" +
            "-cum-Deputy Secretary to Govt., Information and Public Relation Deapartment, Odisha, Bhubanesawr with Request for publication of the advertisement before." + draftTenderNotice.getTenderPublicationDate() + "in Two English National Daily and Two Odia Daily NewsPaper" +
            "The cost of advertisement will be paid by OCTDMS on submission of the bills.";
    Paragraph paragraph3 = new Paragraph(footer, font);
    paragraph3.setAlignment(Paragraph.ALIGN_LEFT);
    paragraph3.setSpacingBefore(5);
    String footer2 = "Encl:So ft Copy of the Advertisement in CD.";
    Paragraph paragraph6 = new Paragraph(footer2, fontTitle);
    paragraph6.setAlignment(Paragraph.ALIGN_LEFT);
    paragraph6.setSpacingBefore(4);
   *//*Paragraph paragraph1 = new Paragraph("This is my Paragraph", fontParagraph);
        paragraph1.setAlignment(Paragraph.ALIGN_LEFT);
*//*
    document.add(paragraph);
    document.add(paragraph1);
    document.add(paragraph4);
    document.add(projectInfo);
    document.add(UNDERLINE);
    document.add(p7);
    document.add(UNDERLINE2);
    Chunk glue3 = new Chunk(new VerticalPositionMark());
    Paragraph p3 = new Paragraph("");
    p3.add(new Chunk(glue3));
    p3.add("Project Director,OCTDMS");
    p3.setSpacingBefore(15);
    Chunk glue4 = new Chunk(new VerticalPositionMark());
    Paragraph p4 = new Paragraph("");
    p4.add(new Chunk(glue4));
    p4.add("Project Director,OCTDMS");
    p4.setSpacingBefore(30);
    LineSeparator UNDERLINE3 = new LineSeparator(1f, 20, Color.BLACK, Element.ALIGN_LEFT, -10);
    Chunk glue6 = new Chunk(new VerticalPositionMark());
    Paragraph p6 = new Paragraph(draftTenderNotice.getBidId());
    p6.add(new Chunk(glue6));
    p6.add("");
    p6.setSpacingBefore(12);
    // document.add(paragraph1);
    *//*Chunk glue = new Chunk(new VerticalPositionMark());
    Paragraph p = new Paragraph("Date of Certificate:            01.01.2022");
    p.add(new Chunk(glue));
    p.add("Certificate ID:PRA-SBP-02-0322-001");
    p.setSpacingBefore(25);
    document.add(p);
    Paragraph paragraph2 = new Paragraph("Work Executing Office:  Minor Irrigation Division, Sambalpur", fontParagraph);
    paragraph2.setSpacingBefore(10);
    document.add(paragraph2);
    Paragraph paragraph3 = new Paragraph("Name of Block:              Dhankauda-02", fontParagraph);
    paragraph3.setSpacingBefore(10);
    document.add(paragraph3);
    Chunk glue2 = new Chunk(new VerticalPositionMark());
    Paragraph paragraph13 = new Paragraph("Ref Agreement No.        .....................................");
    paragraph1.add(new Chunk(glue2));
    paragraph1.add("Package ID:  ...........................");
    paragraph1.setSpacingBefore(10);
    //document.add(paragraph1);*//*
    PdfPTable pdfTable = new PdfPTable(2);
    pdfTable.setSpacingBefore(30);
    pdfTable.setWidthPercentage(100);
    writeTableHeader(pdfTable, tenderId);
    writeTableData(pdfTable, tenderId);
    document.add(pdfTable);
    document.add(p4);
    document.add(p5);
    document.add(paragraph3);
    document.add(paragraph6);
    document.add(p3);
    document.add(UNDERLINE3);
    document.add(p6);
    document.close();
  }*/

    @Override
    public void formG2(HttpServletResponse exportResponse, Integer finYrId, String issueNo, Integer bidderId) throws IOException, DocumentException {

        Document document = new Document(PageSize.A3);
        PdfWriter.getInstance(document, exportResponse.getOutputStream());
       FormGDto formG = tenderRepositoryImpl.getFormGDetails2(finYrId, issueNo, bidderId);

        Font bold = FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD);

        document.open();
        Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        fontTitle.setSize(13);

        Font font = FontFactory.getFont(FontFactory.HELVETICA);
        font.setColor(Color.BLACK);
        font.isBold();

        document.open();
//        Image image = Image.getInstance("/Users/bhagyashreepalai/bhagya_dev/OIIPCRA/src/main/resources/img/odishaWhite.png");
//        image.scaleAbsolute(70f, 70f);
//        image.setAbsolutePosition(30, 1085);
//
//        Image image1 = Image.getInstance("/Users/bhagyashreepalai/bhagya_dev/OIIPCRA/src/main/resources/img/octdms-logo.png");
//        image1.scaleAbsolute(70f, 70f);
//        image1.setAbsolutePosition(730, 1084);

        Paragraph paragraph = new Paragraph("ODISHA COMMUNITY TANK DEVELOPMENT AND MANAGEMENT SOCIETY BHUBANESWAR", fontTitle);
        paragraph.setAlignment(Paragraph.ALIGN_CENTER);
        String para1 = "7th Floor Rajiv Bhawan,Bhubaneswar-751001, ph.0674-2512421, E-Mail:spuoiipcra.od@gov.in";
        Paragraph paragraph1 = new Paragraph(para1, font);
        //LineSeparator UNDERLINE = new LineSeparator(1f, 90, Color.BLACK, Element.ALIGN_CENTER, -10);
        paragraph1.setAlignment(Paragraph.ALIGN_CENTER);
//        paragraph1.add(image);
//        paragraph1.add(image1);


       // Chunk glue1 = new Chunk(new VerticalPositionMark());
        Paragraph p1 = new Paragraph("Form-G: ");
        //p1.add(new Chunk(glue1));
        p1.add(new Chunk("Activity Report of Contractors",bold));
        p1.add("");
        p1.setSpacingBefore(40);


        Chunk glue2 = new Chunk(new VerticalPositionMark());
        Paragraph p2 = new Paragraph("Financial Year :   " );
        p2.add(new Chunk(formG.getFinyr(),bold));
        p2.add(new Chunk(glue2));
        p2.add("No./Date of Issue No.    " + issueNo);
        p2.setSpacingBefore(2);


        Chunk glue3 = new Chunk(new VerticalPositionMark());
        Paragraph p3 = new Paragraph("Name of Agency :   " + formG.getAgencyName() + "," + formG.getLicenseClass());
        p3.add(new Chunk(glue3));
        p3.add("GSTIN of Agency:   " + formG.getGstinNo());
        p3.setSpacingBefore(2);

        LineSeparator UNDERLINE = new LineSeparator(1f, 100, Color.BLACK, Element.ALIGN_CENTER, -10);

        Chunk glue4 = new Chunk(new VerticalPositionMark());
        Paragraph p4 = new Paragraph("Sl No:");
        p4.add(new Chunk(glue4));
        p4.setSpacingBefore(1);
        p4.setSpacingAfter(5);
        p4.add("Bid Id/Name Of Work/");
        p4.setSpacingBefore(5);
        p4.setSpacingAfter(5);
        p4.add("District/Block");
        p4.setSpacingBefore(5);
        p4.add("  Scheme");
        p4.add("  Date opening");
        p4.add("  Estimated Cost");
        p4.add("  Validity of");
        p4.add("  Awarded with ");
        p4.add("  Legal Case");
        p4.add("  Remarks");
        p4.setSpacingBefore(5);

        Chunk glue5 = new Chunk(new VerticalPositionMark());
        Paragraph p5 = new Paragraph("       ");
        p5.add(new Chunk(glue5));
        p5.add("Supply/Package ID                                                  ");
        p5.add("  ");
        p5.add(" ");
        p5.add("    of Tender         put to tender                                                                                   ");
   /* p5.add("Tender                                                  ");
    p5.add("  if any ");
    p5.add("  initiated if");
    p5.add("   ");*/
        p5.setSpacingBefore(2);


        LineSeparator UNDERLINE2 = new LineSeparator(3f, 100, Color.BLACK, Element.ALIGN_CENTER, -10);

        Chunk glue6 = new Chunk(new VerticalPositionMark());
        Paragraph p6 = new Paragraph(" " + formG.getBidId());
        //  p6.add(new Chunk(glue6));
        p6.setSpacingBefore(1);
        p6.setSpacingAfter(5);
        p6.add(" " + formG.getBid_workId());
        p6.add("" + formG.getDistrict());
        p6.add("   " + formG.getScheme());
//        p6.add("   " + formG.getTenderOpeningDate());
        p6.add("    " + formG.getTenderAmount());
        p6.add("      " + formG.getValidityOfTender());
        p6.add("                         ");
        p6.add("                         ");
        p6.add("                         ");
        p6.setSpacingBefore(20);

        LineSeparator UNDERLINE3 = new LineSeparator(1f, 100, Color.BLACK, Element.ALIGN_CENTER, -10);

        Chunk glue7 = new Chunk(new VerticalPositionMark());
        Paragraph p7 = new Paragraph("Summary of Agency:(1 deatils Record)");
        p7.add(new Chunk(glue7));
        p7.add("Total Cost of tender paricipated:              " + formG.getTenderAmount());
        p7.setSpacingBefore(20);

        LineSeparator UNDERLINE4 = new LineSeparator(1f, 100, Color.BLACK, Element.ALIGN_CENTER, -10);


        Chunk glue8 = new Chunk(new VerticalPositionMark());
        Paragraph p8 = new Paragraph("  ");
        p8.add(new Chunk(glue8));
        p8.add("Executive Engineer, Mon, " + formG.getScheme());
        p8.setSpacingBefore(40);

        document.add(paragraph);
        document.add(paragraph1);
        document.add(p1);
        document.add(p2);
        document.add(p3);
        document.add(UNDERLINE);
        document.add(p4);
        document.add(p5);
        document.add(UNDERLINE2);
        document.add(p6);
        document.add(UNDERLINE3);
        document.add(p7);
        document.add(UNDERLINE4);
        document.add(p8);
        document.close();

    }


    public File technicalBidAbstract(HttpServletResponse exportResponse, Integer bidId, java.sql.Date bidOpeningDate, Integer bidStatus, Integer userId) throws Exception {


        Context context = new Context();

        context.setVariable("Tender", tenderRepositoryImpl.getBidIdAndTechnicalDate(bidId));
        context.setVariable("stipulation", tenderRepositoryImpl.getTenderStipulation(bidId));
        context.setVariable("getDate", tenderRepositoryImpl.getCurrentDate());
        context.setVariable("allData", tenderRepositoryImpl.getTechnicalBidAbstract(bidId, bidOpeningDate, bidStatus));
        context.setVariable("bidOpeningDate", bidOpeningDate);//extra data
        context.setVariable("bidId", bidId);
        //office data
        UserInfoDto user = userQueryRepository.getUserById(userId);
        Integer distId = null;
        if (user.getUserLevelId() == 1) {
            distId = 0;
        }
        List<Integer> disIds = null;
        if (user.getUserLevelId() == 2) {
            disIds = officeDataService.getDistId(userId);
        }
        Integer divisionId = null;
        if (user.getUserLevelId() == 6) {
            // distId = officeDataService.getDistId(userId);
            divisionId = officeDataRepositoryImpl.getDivisionIdByUserId(userId);
        }
        context.setVariable("OfficeData", officeDataService.getOfficeDataDetails(distId, divisionId, userId, disIds));

        String divisionName="";
        List<OfficeDataDto> getDivisionName = officeDataService.getOfficeDataDetails(distId, divisionId, userId, disIds);
        if(user.getRoleId()<=4){
            context.setVariable("DivName", divisionName);
        } else {
            for(OfficeDataDto divName: getDivisionName){
                divisionName=","+" ("+ divName.getDivisionName()+")";
                context.setVariable("DivName", divisionName);
            }
        }

        String officeName = " ";
        if(user.getUserLevelId() ==1)
        {
            officeName = "Odisha Community Tank Development and Management Society, Bhubaneswar " ;
        }
        if(user.getUserLevelId() == 6)
        {
            for(OfficeDataDto office: getDivisionName){
                officeName="Minor Irrigation Division,"+" ("+ office.getDivisionName()+")";
                context.setVariable("officeName", officeName);
            }
        }

        context.setVariable("officeName",officeName);

        String html = templateEngine.process("technical-bid-abstract", context);
        String pdfName = "TechnicalBidAbstract";
        return renderPdfFinancialBidResult(html, pdfName);
    }

    public File financialBidOpeningResult(HttpServletResponse exportResponse, Integer bidId, java.sql.Date finBidOpeningDate,Integer userId) throws Exception {

        Context context = new Context();
        context.setVariable("Tender", tenderRepositoryImpl.getBidIdAndFinancialDate(bidId));
        context.setVariable("FinancialBidResultDto", tenderRepositoryImpl.getFinancialBidResult(bidId, finBidOpeningDate));
        List<FinancialBidResultDto> finBid = tenderRepositoryImpl.getFinancialBidResult(bidId, finBidOpeningDate);


//        for (int j = 0; j < finBid.size(); j++) {
//
//             Double year = finBid.get(j).getTimeForCompletion()/ 365 ;
//             Double capacity = finBid.get(j).getCapacity() * year ;
//             String maxBid = tenderRepositoryImpl.getStringValue(capacity);
//             finBid.get(j).setMaxCapacity2(maxBid);
//        }


        int length = finBid.toArray().length;
        String name = " ";
        if (length <= 1) {
            name = " detail Record";
        } else {
            name = "detail Records";
        }
        context.setVariable("name", name);

        UserInfoDto user = userQueryRepository.getUserById(userId);
        Integer distId = null;
        if (user.getUserLevelId() == 1) {
            distId = 0;
        }
        List<Integer> disIds = null;
        if (user.getUserLevelId() == 2) {
            disIds = officeDataService.getDistId(userId);
        }
        Integer divisionId = null;
        if (user.getUserLevelId() == 6) {
            // distId = officeDataService.getDistId(userId);
            divisionId = officeDataRepositoryImpl.getDivisionIdByUserId(userId);
        }


        context.setVariable("OfficeData", officeDataService.getOfficeDataDetails(distId, divisionId, userId, disIds));

        String divisionName="";
        List<OfficeDataDto> getDivisionName = officeDataService.getOfficeDataDetails(distId, divisionId, userId, disIds);
        if(user.getRoleId()<=4){
            context.setVariable("DivName", divisionName);
        } else {
            for(OfficeDataDto divName: getDivisionName){
                divisionName=","+" ("+ divName.getDivisionName()+")";
                context.setVariable("DivName", divisionName);
            }


        }

        String officeName = " ";
        if(user.getUserLevelId() ==1)
        {
            officeName = "Odisha Community Tank Development and Management Society, Bhubaneswar " ;
        }
        if(user.getUserLevelId() == 6)
        {
            for(OfficeDataDto office: getDivisionName){
                officeName="Minor Irrigation Division,"+" ("+ office.getDivisionName()+")";
                context.setVariable("officeName", officeName);
            }
        }

        context.setVariable("officeName",officeName);
        String html = templateEngine.process("financial-bid-opening-result", context);//name
        String pdfName = "FinancialBidResult";
        return renderPdf(html, pdfName);
    }

    private File renderPdf(String html, String pdfName) throws Exception {
        File file = File.createTempFile(pdfName, ".pdf");
        OutputStream outputStream = new FileOutputStream(file);
        ITextRenderer renderer = new ITextRenderer(20f * 4f / 3f, 18);
        renderer.setDocumentFromString(html, new ClassPathResource(PDF_RESOURCES).getURL().toExternalForm());
        renderer.layout();
        renderer.createPDF(outputStream);
        outputStream.close();
        file.deleteOnExit();
        return file;
    }

    private File renderPdf2(String html, String pdfName) throws Exception {
        File file = File.createTempFile(pdfName, ".pdf");
        OutputStream outputStream = new FileOutputStream(file);
        ITextRenderer renderer = new ITextRenderer(20f * 4f / 3f, 17);
        renderer.setDocumentFromString(html, new ClassPathResource(PDF_RESOURCES).getURL().toExternalForm());
        renderer.layout();
        renderer.createPDF(outputStream);
        outputStream.close();
        file.deleteOnExit();
        return file;
    }

    private File renderPdfEngineerDatabse(String html, String pdfName) throws Exception {
        File file = File.createTempFile(pdfName, ".pdf");
        OutputStream outputStream = new FileOutputStream(file);
        ITextRenderer renderer = new ITextRenderer(20f * 4f / 3f, 18);
        renderer.setDocumentFromString(html, new ClassPathResource(PDF_RESOURCES).getURL().toExternalForm());
        renderer.layout();
        renderer.createPDF(outputStream);
        outputStream.close();
        file.deleteOnExit();
        return file;
    }

    private File renderPdf2(String html1, String html2, String html3, String mergedFileName) throws Exception {
        File file = File.createTempFile(mergedFileName, ".pdf");
        OutputStream outputStream = new FileOutputStream(file);
        ITextRenderer renderer = new ITextRenderer(20f * 4f / 3f, 18);
        String html = html1 + html2 + html3;
        renderer.setDocumentFromString(html, new ClassPathResource(PDF_RESOURCES).getURL().toExternalForm());
//        renderer.setDocumentFromString(html2, new ClassPathResource(PDF_RESOURCES).getURL().toExternalForm());
//        renderer.setDocumentFromString(html3, new ClassPathResource(PDF_RESOURCES).getURL().toExternalForm());
        renderer.layout();
        renderer.createPDF(outputStream);
        outputStream.close();
        file.deleteOnExit();
        return file;
    }

    private File renderPdfFinancialBidResult(String html, String pdfName) throws Exception {
        File file = File.createTempFile(pdfName, ".pdf");
        OutputStream outputStream = new FileOutputStream(file);
        ITextRenderer renderer = new ITextRenderer(20f * 4f / 3f, 12);
        renderer.setDocumentFromString(html, new ClassPathResource(PDF_RESOURCES).getURL().toExternalForm());
        renderer.layout();
        renderer.createPDF(outputStream);
        outputStream.close();
        file.deleteOnExit();
        return file;
    }
//  private Context getContext(HttpServletResponse exportResponse, String bidId, java.sql.Date bidOpeningDate) {
//
//    Context context = new Context();
//    //context.setVariable("students", studentRepository.findById(tenderId));
//    context.setVariable("allData", tenderRepositoryImpl.getFinancialBidAbstract(bidId,bidOpeningDate));
//    context.setVariable("issueNo", bidOpeningDate);
//    return context;
//  }


    //  private String loadAndFillTemplate(Context context) {
//    return templateEngine.process("pdf-invoice", context);
//  }
//    @Override
//    public void TechnicalBidOpeningResult(HttpServletResponse exportResponse, Integer bidId, String bidOpeningDate) throws IOException, DocumentException, ParseException {
//
//        TenderInfo tender=tenderRepositoryImpl.getBidIdAndTechnicalDate(bidId);
//
//        Document document = new Document(PageSize.A3);
//        PdfWriter.getInstance(document, exportResponse.getOutputStream());
//
//        com.lowagie.text.Font font = FontFactory.getFont(FontFactory.HELVETICA);
//        font.setColor(Color.BLACK);
//        font.isBold();
//
//        Font bold = FontFactory.getFont(FontFactory.HELVETICA, 12.5f, Font.BOLD);
//        Font bold2 = FontFactory.getFont(FontFactory.HELVETICA, 16, Font.BOLD);
//
//        document.open();
//
//        String para1 = "Technical Bid Opening  in Tender ID:"+tender.getBidId()+"         Technical Bid Opened on Dt:"+tender.getTechBidDate()+"     "
//                + System.lineSeparator() ;
//        String para2= "Odisha Community Tank Development and Management Society, Bhubaneswar";
//        Paragraph paragraph1 = new Paragraph(para1, bold);
//        Paragraph paragraph2 = new Paragraph(para2, bold2);
//        LineSeparator UNDERLINE = new LineSeparator(1f, 90, Color.BLACK, Element.ALIGN_CENTER, -10);
//        paragraph1.setAlignment(Paragraph.ALIGN_CENTER);
//        paragraph1.setSpacingBefore(20);
//        paragraph2.setAlignment(Paragraph.ALIGN_CENTER);
//        paragraph2.setSpacingBefore(10);
//
//
//        //Table creation for  Information
//        PdfPTable projectInfoPdf = new PdfPTable(1);
//        projectInfoPdf.setWidthPercentage(100);
//        projectInfoPdf.setSpacingBefore(20);
//        projectInfoPdf.setHorizontalAlignment(100);
//
//        //Table creation for Account of Work execute
//        PdfPTable tableAccountWork = new PdfPTable(11);
//        tableAccountWork.setWidthPercentage(100);
//        tableAccountWork.setSpacingBefore(30);
//        writeTableHeaderWorkExecute(tableAccountWork, bidId, bidOpeningDate);
//
//        document.add(paragraph1);
//        document.add(paragraph2);
//        document.add(tableAccountWork);
//        document.close();
//
//    }
//
//    private void writeTableHeaderWorkExecute(PdfPTable tableAccountWork, Integer bidId, String bidOpeningDate) {
//
//        Font font = FontFactory.getFont(FontFactory.HELVETICA);
//        font.setColor(Color.RED);
//
//        tableAccountWork.setWidthPercentage(100);
//        PdfPCell cell1 = createCell("Work Sl in TCN.", 1, 2, PdfPCell.BOX);
//        cell1.setVerticalAlignment(5);
//        tableAccountWork.addCell(cell1);
//        PdfPCell cell2 = createCell("Work Id", 1, 2, PdfPCell.BOX);
//        cell2.setVerticalAlignment(5);
//        tableAccountWork.addCell(cell2);
//        PdfPCell cell3 = createCell("Amount Put to tender in Rs.", 1, 2, PdfPCell.BOX);
//        cell3.setVerticalAlignment(5);
//        tableAccountWork.addCell(cell3);
//        PdfPCell cell4 = createCell("Name Of Agency", 1, 2, PdfPCell.BOX);
//        cell4.setVerticalAlignment(5);
//        tableAccountWork.addCell(cell4);
//        PdfPCell cell5 = createCell("Class of License", 1, 2, PdfPCell.BOX);
//        cell5.setVerticalAlignment(5);
//        tableAccountWork.addCell(cell5);
//        PdfPCell cell6 = createCell("License Validity", 1, 2, PdfPCell.BOX);
//        cell6.setVerticalAlignment(5);
//        tableAccountWork.addCell(cell6);
//        PdfPCell cell7 = createCell("EMD Deposited in Rs", 1, 2, PdfPCell.BOX);
//        cell7.setVerticalAlignment(5);
//        tableAccountWork.addCell(cell7);
//        PdfPCell cell8 = createCell("Max Turnover in Lakh Rs", 1, 2, PdfPCell.BOX);
//        cell8.setVerticalAlignment(5);
//        tableAccountWork.addCell(cell8);
//        PdfPCell cell9 = createCell("Credit Availability in Lakh Rs.", 1, 2, PdfPCell.BOX);
//        cell9.setVerticalAlignment(5);
//        tableAccountWork.addCell(cell9);
//        PdfPCell cell10 = createCell("Financial Validity/Work Experience.", 1, 2, PdfPCell.BOX);
//        cell10.setVerticalAlignment(5);
//        tableAccountWork.addCell(cell10);
//        PdfPCell cell11 = createCell("Overall Bid Validity", 1, 2, PdfPCell.BOX);
//        cell11.setVerticalAlignment(5);
//        tableAccountWork.addCell(cell11);
//
//
//
//    }
//
//    public PdfPCell createCell(String content, int colspan, int rowspan, int border) {
//        PdfPCell cell = new PdfPCell(new Phrase(content));
//        cell.setColspan(colspan);
//        cell.setRowspan(rowspan);
//        cell.setBorder(border);
//        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//        cell.setVerticalAlignment(Element.ALIGN_CENTER);
//        return cell;
//    }

    public File getFinancialBidOpeningAbstract(HttpServletResponse exportResponse, Integer bidId, java.sql.Date tenderOpeningDate, Integer userId) throws Exception {
        Context context = new Context();
        List<FinancialBidopeningAbstractDto> list= tenderRepositoryImpl.getFinancialBidOpeningAbstract(bidId, tenderOpeningDate);
        FinancialBidopeningAbstractDto financial = tenderRepositoryImpl.getSumOfEstdCostandBidprice(bidId, tenderOpeningDate);
        TenderInfo tender = tenderRepositoryImpl.viewTenderByTenderId(bidId);

        for(int i=0;i<list.size();i++){

                if (list.get(i).getBar().trim().equals(".00")) {
                    list.get(i).setBar("0.00");
                }
                list.get(i).setFinalQuoted(String.valueOf(list.get(i).getQuoted()) + "%");

        }
        context.setVariable("FinancialData",list);
        context.setVariable("bidId", tender.getBidId());
        context.setVariable("tenderOpeningDate", tenderOpeningDate);
        context.setVariable("standardDate", new Date());
        context.setVariable("sumEstdCost", financial.getSumTm());
        context.setVariable("sumBidPrice", financial.getSumBidP());
        UserInfoDto user = userQueryRepository.getUserById(userId);
        Integer distId = null;
        if (user.getUserLevelId() == 1) {
            distId = 0;
        }
        List<Integer> disIds = null;
        if (user.getUserLevelId() == 2) {
            disIds = officeDataService.getDistId(userId);
        }
        Integer divisionId = null;
        if (user.getUserLevelId() == 6) {
            // distId = officeDataService.getDistId(userId);
            divisionId = officeDataRepositoryImpl.getDivisionIdByUserId(userId);
        }
        context.setVariable("OfficeData", officeDataService.getOfficeDataDetails(distId, divisionId, userId, disIds));

        String divisionName="";
        List<OfficeDataDto> getDivisionName = officeDataService.getOfficeDataDetails(distId, divisionId, userId, disIds);
        if(user.getRoleId()<=4){
            context.setVariable("DivName", divisionName);
        } else {
            for(OfficeDataDto divName: getDivisionName){
                divisionName=","+" ("+ divName.getDivisionName()+")";
                context.setVariable("DivName", divisionName);
            }
        }

        String html = templateEngine.process("financial-bid-opening-abstract", context);
        String pdfName = "AbstractOfFinancialBidOpening";

        return renderPdf(html, pdfName);
    }

    public File getDraftLetterAward(HttpServletResponse exportResponse, Integer bidId, java.sql.Date technicalBidOpeningDate, java.sql.Date financialBidOpeningDate, Integer DaysAllowedToSign, Integer NoticeIssueNo, Integer userId, Integer distId, Integer divisionId, java.sql.Date userDate) throws Exception {
        Context context = new Context();
        String html = "";
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy ");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        Date date = new Date();
        String s = String.valueOf(userDate);
        LocalDate date1 = LocalDate.parse(s);
        LocalDate date2 = date1.plusDays(DaysAllowedToSign);
        String formattedDate = date2.format(dateTimeFormatter);

        String tech  = String.valueOf(technicalBidOpeningDate);
        LocalDate date3 = LocalDate.parse(tech);
        LocalDate date4 = date3.plusDays(DaysAllowedToSign);
        String techFormatted = date4.format(dateTimeFormatter);

        String expiresDate = String.valueOf(technicalBidOpeningDate);
        LocalDate date5 = LocalDate.parse(expiresDate);
        LocalDate date6 = date5.plusDays(90);
        String expiresDateFormat = date6.format(dateTimeFormatter);



        html += " <!DOCTYPE HTML>\n" +
                "<html xmlns:th=\"http://www.thymeleaf.org\">\n" +
                "\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\"/>\n" +
                "    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\"/>\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"/>\n" +
                "    <title>Document</title>\n" +
                "    <!--  <link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/2.3.2/css/bootstrap.min.css\"/>-->\n" +
                "    <!--  <link href=\"https://cdn.jsdelivr.net/npm/bootstrap@5.2.1/dist/css/bootstrap.min.css\" rel=\"stylesheet\" integrity=\"sha384-iYQeCzEYFbKjA/T2uDLTpkwGzCiq6soy8tYaI1GyVh/UjpbCx/TYkiZhlZB6+fzT\" crossorigin=\"anonymous\"/>-->\n" +
                "    <!--  <link rel=\"stylesheet\" th:href=\"@{../pdf-resources/css/bootstrap2.css}\"/>-->\n" +
                "\n" +
                "    <style>\n" +
                "        .container {\n" +
                "\n" +
                "             width: 760px;\n" +
                "\n" +
                "              margin-right: auto;\n" +
                "    margin-left: auto;\n" +
                "    *zoom: 1;\n" +
                "        }\n" +
                "        .container:before,\n" +
                ".container:after {\n" +
                "    display: table;\n" +
                "    line-height: 0;\n" +
                "    content: \"\"\n" +
                "}\n" +
                "\n" +
                ".container:after {\n" +
                "    clear: both\n" +
                "}\n" +
                "@media print {\n" +
                "  .new-page {\n" +
                "    page-break-before: always;\n" +
                "  }\n" +
                "} " +
                "p{\n" +
                "font-size:14px;\n" +
                "}\n" +
                "\n" +
                //for page number
//                " @page {\n" +
//                "  @bottom-right {\n" +
//                "    content: \"Page \" counter(page) \" of \" counter(pages);\n" +
//                "  }\n" +
//                "}\n" +
                //rupee symbol
                ".rupee{\n" +
                "    background-position:center;\n" +
                "    padding-left:15px;\n" +
                "<!--    padding-top:20px;-->\n" +
                "    width: 0.1px;\n" +
                "    height: 0.1px;\n" +
                "    background-image: url('../pdf-resources/images/rupee.png');\n" +
                "    display:block;\n" +
                "    background-repeat: no-repeat;\n" +
                "}\n" +
                ".child{\n" +
                "    display: inline-block;\n" +
                "    padding: 1rem 1rem;\n" +
                "    vertical-align: middle;\n" +
                "}" +

                "    </style>\n" +
                "</head>\n";

        //loop(logo)

        List<OfficeDataDto> officeData = officeDataService.getOfficeData(distId, divisionId, userId);
        for(OfficeDataDto od:officeData){
            if(od.getDivisionName()!= null){
                od.setDivisionName(", "+ od.getDivisionName());
            }
            else{
                od.setDivisionName(" ");
            }
        }
        UserInfoDto user = userQueryRepository.getUserById(userId);

        if(user.getUserLevelId() == 1){
            if(officeData.size()>0){
                officeData.get(0).setDistrictName("Khordha");
            }
        }
        List<DraftLetterAwardDto> draftLetterAwardDto = tenderRepositoryImpl.getDraftLetterAward(bidId, technicalBidOpeningDate, financialBidOpeningDate);

        String userDays = "";
        // List<DraftLetterAwardDto> dto = new ArrayList<>();
        for (DraftLetterAwardDto item : draftLetterAwardDto) {
            if (item.getNameOfWork().contains("&")) {
                item.setNameOfWork(item.getNameOfWork().replace("&", "&amp;"));
            }

        }


//        String workName=draftLetterAwardDto.get(0).getNameOfWork();
        if (DaysAllowedToSign == 15) {
            userDays = "Fifteen";
        } else if (DaysAllowedToSign == 7) {
            userDays = "Seven";
        } else if (DaysAllowedToSign == 10) {
            userDays = "Ten";
        }
        for (DraftLetterAwardDto data : draftLetterAwardDto) {
//            for(int i = NoticeIssueNo; i<=draftLetterAwardDto.size(); i++) {
//            Integer memo = NoticeIssueNo + 2;
            Integer issueNo2 = NoticeIssueNo;
            int i = issueNo2;
            i++;
            data.setMemoNumber(i);
            data.setMemoNumber2(data.getMemoNumber()+1);
            data.setMemoNumber3(data.getMemoNumber2()+1);
            data.setMemoNumber4(data.getMemoNumber3()+1);


           if (data.getEeId() != null) {
               if (data.getEeId() == -99) {
                   data.setOtherEe(data.getOtherEe());
                   data.setDesignationName("Executive Engineer");
               } else {
                   data.setOtherEe(data.getUserName());
                   data.setDesignationName(data.getDesignationName());
               }

           }
           if(data.getCharEmdAmount()== null){
               data.setCharEmdAmount("0.00");
           }

            if(data.getCharAmountQuoted()== null){
                data.setCharAmountQuoted("0.00");
            }

//            if(data.getIsdToBeDeposited()== null){
//                data.setCharIsdToBeDeposited("0.00");
//            }

            if(officeData.get(0).getCircleName() == null){
                officeData.get(0).setCircleName("......................");
            }
            if(officeData.get(0).getDivisionName() == null){
                officeData.get(0).setDivisionName(" ");
            }
            if(data.getSubDivisionName()== null) {
                data.setSubDivisionName(".....");
            }

            if(data.getSectionName()== null) {
                data.setSectionName(".....");
            }

            if(data.getSubDivisionOfficerName() == null){
                data.setSubDivisionOfficerName("....................");
            }

            if(user.getUserLevelId()==1) {

                data.setSubDivisionOfficerName(data.getOtherEe() + ", Executive Engineer");
            }
            else {
                data.setSubDivisionOfficerName(data.getSubDivisionOfficerName());
            }



            html += " <body>\n" +
                    "\n" +
                    "<div class=\"container\"><!-- th:each=\"data,iterator : ${DraftLetterAward}\"-->\n" +
                    "    <div class=\"row\">\n" +
//                    "        <div style=\" display: flex; justify-content: space-around;\">\n" +
//                    "            <img  src=\"../pdf-resources/images/logo.png\"  class=\"img-rounded logo\" />\n" +
//                    "\n" +
//                    "\n" +
//                    "            <img  src=\"../pdf-resources/images/logo-text.png\" style=\"margin-left:34px\" class=\"img-rounded logo\" />\n" +
//                    "\n" +
//                    "\n" +
//                    "            <img  src=\"../pdf-resources/images/govtp-logo.png\" style=\"margin-left:40px\" class=\"img-rounded logo\" />\n" +
//                    "\n" +
//                    "        </div>\n" +
                    " <table width=\"100%\" style=\"border-style: none;\">\n" +
                    "            <tr>\n" +
                    "                <td style=\"text-align:left;border-style: none;\"><img src=\"../pdf-resources/images/logo.png\"/></td>\n" +
                    "                <td style=\"text-align:center;border-style: none;\">\n" +
                    "                    <strong> GOVERNMENT OF ODISHA</strong> <br/>\n" +
                    "                    <strong> Department of Water Resources </strong> <br/>\n " +
                    "                    <strong><span> Minor Irrigation Division </span>,  <span> "+ officeData.get(0).getDivisionName() + " </span> </strong> <br/>\n " +
                    "                    <strong><span> Dist:- </span> <span> "+officeData.get(0).getDistrictName() + " </span>, <span>Pin-</span> <span>"+officeData.get(0).getSpuPinNo() +" </span></strong> <br/>\n " +
                    "                    <strong><span> E-mail : </span> <span> "+officeData.get(0).getSpuEmail()+" <span> , </span> </span> </strong>  <strong> Ph:<span>  "+ officeData.get(0).getLandLineNo() + " </span></strong>\n  " +
//                  "                    <strong > "+officeData.get(0).getOfficeName() +" </strong> <br/>\n" +
//                    "                    <strong> <span> "+ officeData.get(0).getSpuAddress() + " </span> <span> "+ officeData.get(0).getSpuPost() + " </span>. <span>  "+officeData.get(0).getSpuPinNo() + " </span></strong> <br/>\n" +
//                    "                    Ph:<span> "+ officeData.get(0).getLandLineNo() + " </span>,E-mail:<span> " + officeData.get(0).getSpuEmail() + "</span> <br/>\n" +
                    "\n" +
                    "\n" +
                    "                </td>\n" +
                    "                <td style=\"text-align:right;border-style: none;\"><img src=\"../pdf-resources/images/govtp-logo.png\"/>\n" +
                    "                </td>\n" +
                    "            </tr>\n" +
                    "        </table> " +
                    "    </div>\n" +
                    "    <strong style=\"font-family:sans-serif; margin-left:30%;margin-top:0\"  >(WORLD BANK PROJECT ID: P163533)</strong>\n" +
                    "    <br/>\n" +
//                    "    <span style=\"font-family:sans-serif; margin-left:28%;\"> Ph: "+officeData.get(0).getLandLineNo()+"; &nbsp; E-Mail: "+officeData.get(0).getSpuEmail()+"n</span> <br/>\n" +
                    "    <p style=\"font-family:sans-serif; margin-left:28%;margin-top:0\"> Letter No. &nbsp; <b><span> " + NoticeIssueNo + " </span></b>/<span> " + data.getTenderBidId() + " </span> &nbsp; Date.&nbsp;<b> " + formatter.format(date) + " </b>   " +
                    " </p> ";

            html += "<div  class=\"container\">\n" +
                    "\n" +
                    "\n" +
                    "        <p style=\"font-family:sans-serif;\">From</p>\n" ;


             if(user.getUserLevelId() ==1)
                    {
            html  +=  " <p style=\" font-family:sans-serif; margin-left:40px\"> <strong>" + officeData.get(0).getHeadOfOffice() + "</strong>  <br/> " + officeData.get(0).getDesignationName() + " </p>\n" ;
                    }
                    else {
            html  += " <p style=\" font-family:sans-serif; margin-left:40px\"> <strong>" + officeData.get(0).getHeadOfOffice() + "</strong>  <br/> " + officeData.get(0).getDesignationName() + ", Minor Irrigation Division  " + officeData.get(0).getDivisionName() + " </p>\n " ;
                    }



            html  +=  " <p style=\" font-family:sans-serif;\">To</p>\n" +
                    " <p  style=\"font-family:sans-serif; margin-left:40px\"> <strong>" + data.getAgencyName() + ",&nbsp; " + data.getLicenseClass() + " &nbsp;Contractor. </strong> <br/> At: " + data.getAddress() + ", Po: " + data.getPost() + " <br/>District: " + data.getDistrictName() + ".&nbsp;PIN: " + data.getPinCode() + "</p>\n";

            html += "<p style=\" font-family:sans-serif;\"><strong>Sub: </strong>&nbsp;Letter of Acceptance-cum-Notice to Proceed with the Work for <strong>" + data.getWorkId() + "-" + data.getNameOfWork() + "</strong> " +
                    "in " + data.getDistrictName() + " District under the Scheme <strong>" + data.getProjectName() + "</strong>.</p>" +
                    "<p style=\" font-family:sans-serif;\">\n" +
                    "        <span><strong>Ref: &nbsp;</strong>\n" +
                    "        <span> This office tender vide Bid ID: <strong>" + data.getTenderBidId() + "</strong> opened on Dt: <strong>" + data.getDateStringForTenderOpen() + "</strong> and Financial Bid\n" +
                    "            opened on Dt: <strong>" + data.getDateStringForFinBid() + "</strong>.</span></span></p>\n";
            html += "<p style=\" font-family:sans-serif;\">Dear Madam/Sir,</p>\n" +
                    "        <p style=\"font-family:sans-serif; text-align: justify;word-wrap: break-word;\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;As per the out come of the Financial Bid Opening for the work referred above, your bid was\n" +
                    "            declared as <strong>" + data.getBidPosition() + "</strong> and considered to be awarded in your favour. In this regard, you are requested\n" +
                    "            to attend the office of the <br/> \n " +
                    "            <strong> "+officeData.get(0).getDesignationName()+", Minor Irrigation Division, " + data.getDivision() + "</strong> for execution of the\n" +
                    "            Agreement by producing the documents/ credentials in original for verification\n " ;

            if(data.getBalance() != null && data.getBalance()!= 0){

                html += " and depositing the\n" +
                        " amount of <strong>\"Additional Performance Security (APS)\"</strong> for the less bid amount " ;
             }

            html += "   and <strong>\"Initial Security\n" +
                    "   Deposit (ISD)\"</strong>indicated as under <strong> within " + DaysAllowedToSign + " (" + userDays + ") days</strong> of receipt of this intimation.</p>";

            String amountString2 = convertToIndianCurrency(data.getTimeForCompletion());
            Integer time = data.getTimeForCompletion().intValue();

            html += "<div style=\" padding: 0 20px;\"><p style=\" font-family:sans-serif;\"> <span >Amount put to tender:&nbsp; &nbsp;<span> <strong><span class=\"rupee child\"></span><span class=\"child\">" + data.getCharTenderAmount() + "</span>     </strong></span></span> <span style=\"float:right\">APS to be deposited:&nbsp;<span><strong><span class=\"rupee child\"></span><span class=\"child\"> " + data.getCharEmdAmount() + "</span></strong></span></span></p>\n" +
                    "<p style=\" font-family:sans-serif;\"> <span >Rate Quoted:&nbsp;<span><strong><span class=\"rupee child\"></span><span class=\"child\"> " + data.getCharAmountQuoted() + "</span></strong></span></span> <span style=\"float:right\">ISD to be deposited @2.5%:&nbsp; <strong>₹<span class=\"rupee child\"></span><span class=\"child\">" + data.getIsd() + "</span></strong></span></p></div>\n" +
                    "\n" +
                    "        <p style=\"font-family:sans-serif; text-align: justify;word-wrap: break-word;\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; You are requested to sign the Contract agreement and proceed with the work by <strong>Dt: <span>"  +formattedDate+ " </span></strong>\n" +
                    "            under the instructions of the Engineer-in-Charge intimated herewith. You are also requested to submit a\n" +
                    "            <strong> \"Fortnightly Work Programme\"</strong> at the time of signing the contract and ensure completion of the work\n" +
                    "            within the contract period of <strong> " +time +" ("+amountString2+") days </strong> from the Date of Commencement of the work. </p>\n" +
                    "        <p style=\"font-family:sans-serif; text-align: justify;word-wrap: break-word;\"> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;The date of Tender Validity to sign the Contract agreement expires on Dt: <strong> "+expiresDateFormat+" </strong> With the\n" +
                    "            issuance of this acceptance letter and your furnishing the Performance Security, the contract for the\n" +
                    "            above said work stands concluded.</p>";

            html += "<div style=\"font-family:sans-serif; float:left;  width:49%;  display:inline-block;\">\n" +
                    "            <p style=\"font-family:sans-serif; text-align: center;\"> <span >Engineer-in-charge: <strong>" + data.getSubDivisionOfficerName() + " </strong></span></p>\n" +
                    "            <p style=\"font-family:sans-serif; text-align: center;\"> <span >Minor Irrigation,Sub Division: <strong>" +  data.getDivision()  + "</strong></span></p>\n" +
                    "        </div>\n" +
                    "        <div style=\"font-family:sans-serif;  width:49%;float:left;\">\n" +
                    "            <div style=\"font-family:sans-serif; text-align: center;\">\n" +
                    "                <p style=\"font-family:sans-serif;\">Yours Faithfully,</p>\n" +
                    "               <br/>\n" +
                    "                <p style=\"font-family:sans-serif; line-height: normal;\"> " + officeData.get(0).getHeadOfOffice() + "<br/>\n" +
                    "                " + officeData.get(0).getDesignationName() + "," +data.getDivision()+ "</p>\n" +
                    "            </div>\n" +
                    "\n" +
                    "        </div>\n" +

//                    "        <p style=\"font-family:sans-serif;\">Memo No. " + memo + " &nbsp; &nbsp;&nbsp;&nbsp; <span> Date: <b>" + formatter.format(date) + "</b> </span></p>\n" +
//                    "\n" +
//                    "        <p style=\"font-family:sans-serif; text-align: justify;word-wrap: break-word;\"> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Copy to the Superintending Engineer, SMI Circle, Berhampur/ Executive Engineer, Minor Irrigation\n" +
//                    "            Division, Bhanjanagar for information and necessary action.</p>\n" +
//                    "<p style=\"font-family:sans-serif; float:right\">"+officeData.get(0).getDesignationName()+"</p>" +
                    " <div> <p style=\"font-family:sans-serif;\">Memo No. " + data.getMemoNumber() + " &nbsp; &nbsp;&nbsp;&nbsp; <span> Date: <b>" + formatter.format(date) + "</b> </span></p>\n" +
                    "        <p style=\"font-family:sans-serif;\"> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Copy submitted to the Project Director, OCTDMS for information and necessary action.</p> \n" +
                    "        <p style=\"font-family:sans-serif; float:right; \">"+officeData.get(0).getDesignationName()+  "," +data.getDivision()+ "</p>" +
                    " </div><br/>" +
                    " <div>  <p style=\"font-family:sans-serif;\">Memo No. " + data.getMemoNumber2() + " &nbsp; &nbsp;&nbsp;&nbsp; <span> Date: <b>" + formatter.format(date) + "</b> </span></p>\n" +
                    "        <p style=\"font-family:sans-serif;\"> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Copy submitted to the Chief Engineer, Minor Irrigation for information  and necessary action.</p>  \n" +
                    "        <p style=\"font-family:sans-serif; float:right\">"+officeData.get(0).getDesignationName()+ "," +data.getDivision()+ "</p>" +
                    "</div><br/>" +

                    "<div>   <p style=\"font-family:sans-serif;\">Memo No. " + data.getMemoNumber3() + " &nbsp; &nbsp;&nbsp;&nbsp; <span> Date: <b>" + formatter.format(date) + "</b> </span></p>\n" +

                    "        <p style=\"font-family:sans-serif;\"> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Copy submitted to the Additional Chief Engineer, "+officeData.get(0).getCircleName() + " for  information and necessary action.  </p> " +
                       "    <p style=\"font-family:sans-serif; float:right\">"+officeData.get(0).getDesignationName()+ "," +data.getDivision()+"</p>" +
                    "</div><br/>" +

                    "<div>   <p style=\"font-family:sans-serif;\">Memo No. " + data.getMemoNumber4() + " &nbsp; &nbsp;&nbsp;&nbsp; <span> Date: <b>" + formatter.format(date) + "</b> </span></p>\n" +
                    "\n" +
                    "    <p style=\"font-family:sans-serif; text-align: justify;word-wrap: break-word;\"> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Copy forwarded to the Assistant Executive Engineer M.I.Sub-Division, " +data.getSubDivisionName() + "  / Assistant Engineer M.I.Section, " +data.getSectionName() + " for information and necessary action.  </p>  \n" +
                    "    <p style=\"font-family:sans-serif; float:right\">"+officeData.get(0).getDesignationName()+ "," +data.getDivision()+ "</p><br/>" +
                    "</div><br/>" +



                    " </div>" + //form div end
                    "</div> " + //logo div end
                    "<p class=\"new-page\"></p>  "+
                    "</body>\n";
//            }
        }

        //foot
        html += "</html>";


//        context.setVariable("DraftLetterAward", tenderRepositoryImpl.getDraftLetterAward(bidId, technicalBidOpeningDate, financialBidOpeningDate));
//        context.setVariable("OfficeData", officeDataService.getOfficeData(distId, divisionId, userId));
//        context.setVariable("bidId", bidId);
//        context.setVariable("userDate", userDate);
//        context.setVariable("technicalBidOpeningDate", technicalBidOpeningDate);
//        context.setVariable("financialBidOpeningDate", financialBidOpeningDate);
//        context.setVariable("NoticeIssueNo", NoticeIssueNo);
//        context.setVariable("DaysAllowedToSign", DaysAllowedToSign);
//        String html = templateEngine.process("draft-letter-award", context);

        String pdfName = "DraftLetterAward";
        return renderPdf2(html, pdfName);
    }


//    @Override
//    public File getEngineerDatabasee(HttpServletResponse exportResponse, /*Integer designationId,*/ Integer userId) throws IOException, Exception {
//        SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy ");
//        Date date = new Date();
//        int i = 1;
//        int j=1;
////        List<EngineerDatabaseDto> dto = tenderRepositoryImpl.getEngineerDatabase(designationId, userId);
//
//        String html = "<!DOCTYPE html\n" +
//                "        PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n" +
//                "<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\">\n" +
//                "\n" +
//                "<head>\n" +
//                "    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n" +
//                "    <title>file_1670582888824</title>\n" +
//                "    <meta name=\"author\" content=\"ORSAC\" />\n" +
//                "    <style type=\"text/css\">\n" +
//                //for page
//                ".container {\n" +
//                "\n" +
//                "             width: 760px;\n" +
//                "\n" +
//                "              margin-right: auto;\n" +
//                "    margin-left: auto;\n" +
//                "    *zoom: 1;\n" +
//                "        }\n" +
//                "        .container:before,\n" +
//                ".container:after {\n" +
//                "    display: table;\n" +
//                "    line-height: 0;\n" +
//                "    content: \"\"\n" +
//                "}\n" +
//                "\n" +
//                ".container:after {\n" +
//                "    clear: both\n" +
//                "}\n" +
//                "p{\n" +
//                "font-size:14px;\n" +
//                "}\n" +
//                "@page {\n" +
//                "  @top-right {\n" +
//                "    content: \"Page \" counter(page) \" of \" counter(pages);\n" +
//                "  }\n" +
//                "}"+
//                //
//                "        * {\n" +
//                "            margin: 0;\n" +
//                "            padding: 0;\n" +
//                "            text-indent: 0;\n" +
//                "        }\n" +
//                "\n" +
//                "        .s1 {\n" +
//                "            color: #011A2A;\n" +
//                "            font-family: Calibri, sans-serif;\n" +
//                "            font-style: normal;\n" +
//                "            font-weight: normal;\n" +
//                "            text-decoration: none;\n" +
//                "            font-size: 20pt;\n" +
//                "        }\n" +
//                "\n" +
//                "        .s2 {\n" +
//                "            color: #011A2A;\n" +
//                "            font-family: Calibri, sans-serif;\n" +
//                "            font-style: normal;\n" +
//                "            font-weight: normal;\n" +
//                "            text-decoration: none;\n" +
//                "            font-size: 13pt;\n" +
//                "            vertical-align: -2pt;\n" +
//                "            margin-left: 30px;"+
//                "        }\n" +
//                "\n" +
//                "        .s3 {\n" +
//                "            color: black;\n" +
//                "            font-family: Calibri, sans-serif;\n" +
//                "            font-style: normal;\n" +
//                "            font-weight: bold;\n" +
//                "            text-decoration: none;\n" +
//                "            font-size: 11pt;\n" +
//                "        }\n" +
//                "\n" +
//                "        p {\n" +
//                "            color: black;\n" +
//                "            font-family: Calibri, sans-serif;\n" +
//                "            font-style: normal;\n" +
//                "            font-weight: bold;\n" +
//                "            text-decoration: underline;\n" +
//                "            font-size: 12pt;\n" +
//                "            margin: 0pt;\n" +
//                "        }\n" +
//                "\n" +
//                "        .s4 {\n" +
//                "            color: black;\n" +
//                "            font-family: Calibri, sans-serif;\n" +
//                "            font-style: normal;\n" +
//                "            font-weight: normal;\n" +
//                "            text-decoration: none;\n" +
//                "            font-size: 11pt;\n" +
//                "        }\n" +
//                "\n" +
//                "        .s5 {\n" +
//                "            color: black;\n" +
//                "            font-family: Calibri, sans-serif;\n" +
//                "            font-style: normal;\n" +
//                "            font-weight: bold;\n" +
//                "            text-decoration: underline;\n" +
//                "            font-size: 12pt;\n" +
//                "        }\n" +
//                "\n" +
//                "        .s6 {\n" +
//                "            color: black;\n" +
//                "            font-family: Calibri, sans-serif;\n" +
//                "            font-style: normal;\n" +
//                "            font-weight: normal;\n" +
//                "            text-decoration: none;\n" +
//                "            font-size: 11pt;\n" +
//                "        }\n" +
//                "\n" +
//                "        table,\n" +
//                "        tbody {\n" +
//                "            vertical-align: top;\n" +
//                "            overflow: visible;\n" +
//                "        }\n" +
//                "    </style>\n" +
//                "</head>\n" +
//                "\n" +
//                "<body>\n" +
//                "<table style=\"border-collapse:collapse;margin-left:7.325pt\" cellspacing=\"0\">\n" +
//                "    <tr style=\"height:39pt\">\n" +
//                "        <td style=\"width:520pt\" colspan=\"4\" bgcolor=\"#B8D6FB\">\n" +
//                "            <p class=\"s1\" style=\"padding-top: 6pt;padding-left: 2pt;text-indent: 0pt;text-align: left;\">\n" +
//                "                Engineers-in-Charge : OIIPCRA <span class=\"s2\">"+formatter.format(date)+"</span></p>\n" +
//                "        </td>\n" +
//                "    </tr>\n" +
//                "    <tr style=\"height:21pt\">\n" +
//                "        <td\n" +
//                "                style=\"width:152pt;border-top-style:solid;border-top-width:1pt;border-left-style:solid;border-left-width:1pt;border-bottom-style:solid;border-bottom-width:1pt;border-right-style:solid;border-right-width:1pt\">\n" +
//                "            <p class=\"s3\" style=\"padding-top: 2pt;padding-left: 1pt;text-indent: 0pt;text-align: left;\">Name of MI\n" +
//                "                Division</p>\n" +
//                "        </td>\n" +
//                "        <td\n" +
//                "                style=\"width:139pt;border-top-style:solid;border-top-width:1pt;border-left-style:solid;border-left-width:1pt;border-bottom-style:solid;border-bottom-width:1pt;border-right-style:solid;border-right-width:1pt\">\n" +
//                "            <p class=\"s3\" style=\"padding-top: 2pt;padding-left: 2pt;text-indent: 0pt;text-align: left;\">Name of EE\n" +
//                "            </p>\n" +
//                "        </td>\n" +
//                "        <td\n" +
//                "                style=\"width:89pt;border-top-style:solid;border-top-width:1pt;border-left-style:solid;border-left-width:1pt;border-bottom-style:solid;border-bottom-width:1pt;border-right-style:solid;border-right-width:1pt\">\n" +
//                "            <p class=\"s3\" style=\"padding-top: 2pt;padding-left: 2pt;text-indent: 0pt;text-align: left;\">Contact No.\n" +
//                "            </p>\n" +
//                "        </td>\n" +
//                "        <td\n" +
//                "                style=\"width:140pt;border-top-style:solid;border-top-width:1pt;border-left-style:solid;border-left-width:1pt;border-bottom-style:solid;border-bottom-width:1pt;border-right-style:solid;border-right-width:1pt\">\n" +
//                "            <p class=\"s3\" style=\"padding-top: 2pt;padding-left: 2pt;text-indent: 0pt;text-align: left;\">Office\n" +
//                "                e-Mail</p>\n" +
//                "        </td>\n" +
//                "    </tr>\n" +
//                "</table>\n";
//        List<EngineerDatabaseDto> c1 = tenderRepositoryImpl.getCircleKBK1(/*designationId,*/ userId);
//
////        for(EngineerDatabaseDto data : c1) {
//            html += "<table style=\"border-collapse:collapse;margin-left:12.62pt\" cellspacing=\"0\">" +
//                    " <tr style=\"height:25pt\">\n" +
//                    "     <td style=\"width:513pt\" colspan=\"5\">\n" +
//                    "           <p class=\"s5\"\n" +
//                    "            style=\"padding-top: 9pt;padding-left: 1pt;text-indent: 0pt;line-height: 13pt;text-align: left;\">" + c1.get(0).getCircleName() + ", " + c1.get(0).getMiDivisionName() + " </p>\n" +
//                    "             </td>\n" +
//                    " </tr>\n" +
//                    " </table>\n";
//
////            List<EngineerDatabaseDto> listDto = tenderRepositoryImpl.getCircleKBK1(/*designationId,*/ userId);
//            for (EngineerDatabaseDto data : c1) {
//                html += "<table>" +
//                        "    <tr style=\"height:33pt\">\n" +
//                        "        <td style=\"width:17pt\">\n" +
//                        "            <p class=\"s4\" style=\"padding-top: 8pt;padding-left: 9pt;text-indent: 0pt;text-align: right;\">" + i++ + "</p>\n" +
//                        "        </td>\n" +
//                        "        <td style=\"width:100pt\">\n" +
//                        "            <p class=\"s4\" style=\"padding-top: 8pt;padding-left: 1pt;text-indent: 0pt;text-align: right;\">" + data.getMiDivisionName() + "</p>\n" +
//                        "        </td>\n" +
//                        "        <td style=\"width:100pt\">\n" +
//                        "            <p class=\"s4\" style=\"padding-top: 8pt;padding-left: 20pt;text-indent: 0pt;text-align: right;\">" + data.getEe() + "</p>\n" +
//                        "        </td>\n" +
//                        "        <td style=\"width:100pt\">\n" +
//                        "            <p class=\"s4\" style=\"padding-top: 8pt;padding-left: 20pt;text-indent: 0pt;text-align: right;\">" + data.getMobileNo() + "</p>\n" +
//                        "        </td>\n" +
//                        "        <td style=\"width:100pt\">\n" +
//                        "            <p class=\"s4\" style=\"padding-top: 8pt;padding-left: 20pt;text-indent: 0pt;width: 25%;\">" + data.getEmial() + "</p>\n" +
//                        "        </td>\n" +
//                        "    </tr>\n" +
//                        " </table>\n";
//            }
////        }
//        List<EngineerDatabaseDto> c2 = tenderRepositoryImpl.getCircleEsternCircle(/*designationId,*/ userId);
//
//            //        for(EngineerDatabaseDto t2 : c2){
//            html += "<table>" +
//                    "<tr style=\"height:25pt\">\n" +
//                    "        <td style=\"width:513pt\" colspan=\"5\">\n" +
//                    "            <p class=\"s5\"\n" +
//                    "               style=\"padding-top: 10pt;padding-left: 1pt;text-indent: 0pt;line-height: 13pt;text-align: left;\">\n" +
//                    "                "+c2.get(0).getCircleName()+", "+c2.get(0).getMiDivisionName()+"</p>\n" +
//                    "        </td>\n" +
//                    "    </tr>\n" +
//                    "</table>" ;
//            List<EngineerDatabaseDto> listDto1 = tenderRepositoryImpl.getEngineerDatabase(/*designationId,*/ userId);
//            for (EngineerDatabaseDto data : listDto1) {
//                html += " <table>   " +
//                        "<tr style=\"height:27pt\">\n" +
//                        "        <td style=\"width:17pt\">\n" +
//                        "            <p class=\"s4\" style=\"padding-top: 8pt;padding-left: 2pt;text-indent: 0pt;text-align: left;\">"+ j++ +"</p>\n" +
//                        "        </td>\n" +
//                        "        <td style=\"width:100pt\">\n" +
//                        "            <p class=\"s4\" style=\"padding-top: 8pt;padding-left: 9pt;text-indent: 0pt;text-align: left;\">"+data.getMiDivisionName()+"</p>\n" +
//                        "        </td>\n" +
//                        "        <td style=\"width:173pt\">\n" +
//                        "            <p class=\"s4\" style=\"padding-top: 8pt;padding-left: 34pt;text-indent: 0pt;text-align: left;\">" + data.getEe() + "\n" +
//                        "                Parida</p>\n" +
//                        "        </td>\n" +
//                        "        <td style=\"width:84pt\">\n" +
//                        "            <p class=\"s4\" style=\"padding-top: 8pt;padding-right: 3pt;text-indent: 0pt;text-align: right;\">" + data.getMobileNo() + "</p>\n" +
//                        "        </td>\n" +
//                        "        <td style=\"width:139pt\">\n" +
//                        "            <p style=\"padding-top: 8pt;padding-left: 4pt;text-indent: 0pt;text-align: left;\"><a\n" +
//                        "                    href=\"mailto:eemidbalangir1@gmail.com\" class=\"s6\">"+data.getEmial()+"</a></p>\n" +
//                        "        </td>" +
//                        "    </tr>\n +" +
//                        "</table>" ;
//            }
//        }


//                            "    <tr style=\"height:25pt\">\n" +
//                            "        <td style=\"width:513pt\" colspan=\"5\">\n" +
//                            "            <p class=\"s5\"\n" +
//                            "               style=\"padding-top: 9pt;padding-left: 1pt;text-indent: 0pt;line-height: 13pt;text-align: left;\">\n" +
//                            "                KBK-II MI Circle, Jeypore</p>\n" +
//                            "        </td>\n" +
//                            "    </tr>\n" +
//                            "    <tr style=\"height:27pt\">\n" +
//                            "        <td style=\"width:17pt\">\n" +
//                            "            <p class=\"s4\" style=\"padding-top: 8pt;padding-left: 2pt;text-indent: 0pt;text-align: left;\">1</p>\n" +
//                            "        </td>\n" +
//                            "        <td style=\"width:100pt\">\n" +
//                            "            <p class=\"s4\" style=\"padding-top: 8pt;padding-left: 9pt;text-indent: 0pt;text-align: left;\">Jeypore</p>\n" +
//                            "        </td>\n" +
//                            "        <td style=\"width:173pt\">\n" +
//                            "            <p class=\"s4\" style=\"padding-top: 8pt;padding-left: 34pt;text-indent: 0pt;text-align: left;\">Subash\n" +
//                            "                Chandra Sethi</p>\n" +
//                            "        </td>\n" +
//                            "        <td style=\"width:84pt\">\n" +
//                            "            <p class=\"s4\" style=\"padding-top: 8pt;padding-right: 4pt;text-indent: 0pt;text-align: right;\">94371\n" +
//                            "                92991</p>\n" +
//                            "        </td>\n" +
//                            "        <td style=\"width:139pt\">\n" +
//                            "            <p style=\"text-indent: 0pt;text-align: left;\"><br /></p>\n" +
//                            "        </td>\n" +
//                            "    </tr>\n" +
//                            "
//                            "    <tr style=\"height:25pt\">\n" +
//                            "        <td style=\"width:513pt\" colspan=\"5\">\n" +
//                            "            <p class=\"s5\"\n" +
//                            "               style=\"padding-top: 10pt;padding-left: 1pt;text-indent: 0pt;line-height: 13pt;text-align: left;\">NMI\n" +
//                            "                Circle, Sambalpur</p>\n" +
//                            "        </td>\n" +
//                            "    </tr>\n" +
//                            "    <tr style=\"height:27pt\">\n" +
//                            "        <td style=\"width:17pt\">\n" +
//                            "            <p class=\"s4\" style=\"padding-top: 8pt;padding-left: 2pt;text-indent: 0pt;text-align: left;\">1</p>\n" +
//                            "        </td>\n" +
//                            "        <td style=\"width:100pt\">\n" +
//                            "            <p class=\"s4\" style=\"padding-top: 8pt;padding-left: 9pt;text-indent: 0pt;text-align: left;\">Padampur</p>\n" +
//                            "        </td>\n" +
//                            "        <td style=\"width:173pt\">\n" +
//                            "            <p class=\"s4\" style=\"padding-top: 8pt;padding-left: 34pt;text-indent: 0pt;text-align: left;\">Jagdish\n" +
//                            "                Chandra Nayak</p>\n" +
//                            "        </td>\n" +
//                            "        <td style=\"width:84pt\">\n" +
//                            "            <p class=\"s4\" style=\"padding-top: 8pt;padding-right: 3pt;text-indent: 0pt;text-align: right;\">94381\n" +
//                            "                84674</p>\n" +
//                            "        </td>\n" +
//                            "        <td style=\"width:139pt\">\n" +
//                            "            <p style=\"padding-top: 8pt;padding-left: 4pt;text-indent: 0pt;text-align: left;\"><a\n" +
//                            "                    href=\"mailto:eemipadampur@gmail.com\" class=\"s6\">eemipadampur@gmail.com</a></p>\n" +
//                            "        </td>\n" +
//                            "    </tr>\n" +
//                            "    <tr style=\"height:17pt\">\n" +
//                            "        <td style=\"width:17pt\">\n" +
//                            "            <p class=\"s4\"\n" +
//                            "               style=\"padding-top: 3pt;padding-left: 2pt;text-indent: 0pt;line-height: 12pt;text-align: left;\">2\n" +
//                            "            </p>\n" +
//                            "        </td>\n" +
//                            "        <td style=\"width:100pt\">\n" +
//                            "            <p class=\"s4\"\n" +
//                            "               style=\"padding-top: 3pt;padding-left: 9pt;text-indent: 0pt;line-height: 12pt;text-align: left;\">\n" +
//                            "                Sambalpur</p>\n" +
//                            "        </td>\n" +
//                            "        <td style=\"width:173pt\">\n" +
//                            "            <p class=\"s4\"\n" +
//                            "               style=\"padding-top: 3pt;padding-left: 34pt;text-indent: 0pt;line-height: 12pt;text-align: left;\">\n" +
//                            "                Santosh Kumar Patnaik</p>\n" +
//                            "        </td>\n" +
//                            "        <td style=\"width:84pt\">\n" +
//                            "            <p class=\"s4\"\n" +
//                            "               style=\"padding-top: 3pt;padding-right: 4pt;text-indent: 0pt;line-height: 12pt;text-align: right;\">\n" +
//                            "                94370 53279</p>\n" +
//                            "        </td>\n" +
//                            "        <td style=\"width:139pt\">\n" +
//                            "            <p style=\"text-indent: 0pt;text-align: left;\"><br /></p>\n" +
//                            "        </td>\n" +
//                            "    </tr>\n" +
//                            "    <tr style=\"height:25pt\">\n" +
//                            "        <td style=\"width:513pt\" colspan=\"5\">\n" +
//                            "            <p class=\"s5\"\n" +
//                            "               style=\"padding-top: 10pt;padding-left: 1pt;text-indent: 0pt;line-height: 13pt;text-align: left;\">SMI\n" +
//                            "                Circle, Berhampur</p>\n" +
//                            "        </td>\n" +
//                            "    </tr>\n" +
//                            "    <tr style=\"height:27pt\">\n" +
//                            "        <td style=\"width:17pt\">\n" +
//                            "            <p class=\"s4\" style=\"padding-top: 8pt;padding-left: 2pt;text-indent: 0pt;text-align: left;\">1</p>\n" +
//                            "        </td>\n" +
//                            "        <td style=\"width:100pt\">\n" +
//                            "            <p class=\"s4\" style=\"padding-top: 8pt;padding-left: 9pt;text-indent: 0pt;text-align: left;\">Bhanjanagar\n" +
//                            "            </p>\n" +
//                            "        </td>\n" +
//                            "        <td style=\"width:173pt\">\n" +
//                            "            <p class=\"s4\" style=\"padding-top: 8pt;padding-left: 34pt;text-indent: 0pt;text-align: left;\">Kishor\n" +
//                            "                Chandra Palo</p>\n" +
//                            "        </td>\n" +
//                            "        <td style=\"width:84pt\">\n" +
//                            "            <p class=\"s4\" style=\"padding-top: 8pt;padding-right: 3pt;text-indent: 0pt;text-align: right;\">94378\n" +
//                            "                58094</p>\n" +
//                            "        </td>\n" +
//                            "        <td style=\"width:139pt\">\n" +
//                            "            <p style=\"padding-top: 8pt;padding-left: 4pt;text-indent: 0pt;text-align: left;\"><a\n" +
//                            "                    href=\"mailto:midbhanjanagar@gmail.com\" class=\"s6\">midbhanjanagar@gmail.com</a></p>\n" +
//                            "        </td>\n" +
//                            "    </tr>\n" +
//                            "
//                            "
//                            "
//                            "</table>\n" +
//                            "<br/>\n" +
//                            "<hr style=\"width: 30%;\" />"+
//                            "<p style=\"text-indent: 0pt;text-align: left;\" />\n" +
//                            "<p style=\"text-indent: 0pt;text-align: left;\" />\n" +
//                            "<p style=\"text-indent: 0pt;text-align: left;\" />\n" +
//                            "<p style=\"text-indent: 0pt;text-align: left;\" />\n" +
//                            "<p style=\"text-indent: 0pt;text-align: left;\" />\n" +
//                            "<p style=\"text-indent: 0pt;text-align: left;\"><br /></p>"+
//                            "<p style=\"padding-left: 6pt;text-indent: 0pt;text-align: left;\" />\n" ;

//                html += "</body>\n" +
//                "\n" +
//                "</html>";
//        String pdfName = "EngineerDatabase";
//        return renderPdfEngineerDatabse(html, pdfName);
//    }

    public File getEngineerDatabase(HttpServletResponse exportResponse, Integer userId) throws Exception {
        UserInfoDto user=userQueryRepository.getUserById(userId);
        Context context = new Context();
        context.setVariable("EngineerDatabase", tenderRepositoryImpl.getCircleKBK1(user.getDesgId()));
        context.setVariable("KBKTwo", tenderRepositoryImpl.getCircleKBK2(user.getDesgId()));
        context.setVariable("CircleEsternCircle", tenderRepositoryImpl.getCircleEsternCircle(user.getDesgId()));
        context.setVariable("NorthernCircle", tenderRepositoryImpl.getNorthernCircle(user.getDesgId()));
        context.setVariable("CentralCircle", tenderRepositoryImpl.getCentralCircle(user.getDesgId()));
        context.setVariable("SouthernCircle", tenderRepositoryImpl.getSouthernCircle(user.getDesgId()));
        context.setVariable("countDivision",tenderRepositoryImpl.getDivisionCount());

        List<EngineerDatabaseDto> dto = tenderRepositoryImpl.getEngineerDatabase(userId);
        context.setVariable("standardDate", new Date());

        String html = templateEngine.process("engineer-database", context);
        String pdfName = "EngineerDatabasePdf";

        return renderPdf(html, pdfName);
    }


    @Override
    public File statusOfMipsByMiDivision(HttpServletResponse exportResponse, Integer districtId, Integer divisionId) throws IOException, Exception {

        Context context = new Context();
        context.setVariable("statusofMipDiv", dashboardQryRepository.getMIPStatusByDivision(districtId, divisionId));
        context.setVariable("districtId", districtId);
        context.setVariable("divisionId", divisionId);

        String html = templateEngine.process("statusofmips-division", context);
        String pdfName = "statusOfMips";

        return renderPdf(html, pdfName);
    }

    @Override
    public File expertPdfStatusOfMipsByDist(HttpServletResponse exportResponse, Integer districtId) throws Exception {

        Context context = new Context();
        context.setVariable("statusofMipDist", dashboardService.getDashboardStatusOfMIPByDist(districtId));
        context.setVariable("districtId", districtId);

        String html = templateEngine.process("statusofmips-Dist", context);
        String pdfName = "statusofMipDist";

        return renderPdf(html, pdfName);
    }

    @Override
    public File expertPdfExpenditureByDist(HttpServletResponse exportResponse, Integer districtId) throws Exception {
        Context context = new Context();
        context.setVariable("expenditureByDist", dashboardService.getDashboardExpenditureInMIPByDistrict(districtId));
        context.setVariable("districtId", districtId);

        String html = templateEngine.process("expenditure-dist", context);
        String pdfName = "expenditureByDist";

        return renderPdf(html, pdfName);
    }

    @Override
    public File expertPdfExpenditureByDiv(HttpServletResponse exportResponse, Integer districtId, Integer divisionId) throws Exception {
        Context context = new Context();
        context.setVariable("expenditureByDiv", dashboardQryRepository.getDashboardExpenditureInMIPByDiv(districtId, divisionId));
        context.setVariable("districtId", districtId);
        context.setVariable("divisionId", divisionId);

        String html = templateEngine.process("expenditure-div", context);
        String pdfName = "expenditureByDiv";

        return renderPdf(html, pdfName);
    }

    @Override
    public File expertPdfComponentEstdAndExp(HttpServletResponse exportResponse) throws Exception {
        Context context = new Context();
        context.setVariable("componentEstdAndExp", dashboardService.getComponentListEstExpList());

        String html = templateEngine.process("component-estd-exp", context);
        String pdfName = "componentEstdAndExp";

        return renderPdf(html, pdfName);
    }

    @Override
    public OIIPCRAResponse getDraftTenderList(TenderListDto tenderListDto) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {
            Page<Tender> draftTenderNotice = tenderRepositoryImpl.getDraftTenderList(tenderListDto);
            List<Tender> tender = draftTenderNotice.getContent();
            result.put("draftTenderNotice", tender);
            result.put("totalItems", draftTenderNotice.getTotalElements());
            response.setData(result);
            response.setStatus(1);
            response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
            response.setMessage(" All DraftTender List");
        } catch (Exception e) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    e.getMessage(),
                    result);
        }
        return response;
    }

    @Override
    public File technicalBidOpeningResult(HttpServletResponse exportResponse, Integer bidId, java.sql.Date bidOpeningDate, Integer distId, Integer divisionId, Integer userId) throws Exception {
        Context context = new Context();

        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy ");
        Date date = new Date();
        String html = " ";
        TenderInfo tender = tenderRepositoryImpl.getBidIdAndTechnicalDate(bidId);
        UserInfoDto user = userQueryRepository.getUserById(userId);

        if (user.getUserLevelId() == 1) {
            distId = 0;
        }
        List<Integer> disIds = null;
        if (user.getUserLevelId() == 2) {
            disIds = officeDataService.getDistId(userId);
        }
        if (user.getUserLevelId() == 6) {
            divisionId = officeDataRepositoryImpl.getDivisionIdByUserId(userId);
        }

        List<OfficeDataDto> officeData = officeDataService.getOfficeDataDetails(distId, divisionId, userId, disIds);

        String heading  = " ";
        if(user.getUserLevelId() ==1)
        {
            heading = "Odisha Community Tank Development and Management Society, Bhubaneswar " ;
        }
        if(user.getUserLevelId() == 6)
        {
            for(OfficeDataDto office: officeData){
                heading="Minor Irrigation Division,"+" ("+ office.getDivisionName()+")";
                context.setVariable("heading", heading);
            }
        }
        html += "<!DOCTYPE HTML>\n" +
                "<html xmlns:th=\"http://www.thymeleaf.org\" xmlns=\"http://www.w3.org/1999/html\" >\n" +
                "\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\"/>\n" +
                "    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\"/>\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"/>\n" +
                "    <title>Document</title>\n" +
                "    <!--  <link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/2.3.2/css/bootstrap.min.css\"/>-->\n" +
                "    <!--  <link href=\"https://cdn.jsdelivr.net/npm/bootstrap@5.2.1/dist/css/bootstrap.min.css\" rel=\"stylesheet\" integrity=\"sha384-iYQeCzEYFbKjA/T2uDLTpkwGzCiq6soy8tYaI1GyVh/UjpbCx/TYkiZhlZB6+fzT\" crossorigin=\"anonymous\"/>-->\n" +
                "    <!--  <link rel=\"stylesheet\" th:href=\"@{../pdf-resources/css/bootstrap2.css}\"/>-->\n" +
                "\n" +
                "    <style>\n" +
                "\n" +
                ".container {\n" +
                "             padding-top: 25px;\n" +
                "             width: 1000px;\n" +
                "             width: auto;\n" +
                "             margin-right: auto;\n" +
                "    margin-left: auto;\n" +
                "    *zoom: 1;\n" +
                "        }\n" +
                "        .container:before,\n" +
                ".container:after {\n" +
                "    display: table;\n" +
                "    line-height:normal;\n" +
                "    content: \"\"\n" +
                "}\n" +
                "\n" +
                "\n" +
                ".table {\n" +
                "            -fs-table-paginate: paginate;\n" +
                "        }" +

                ".container:after {\n" +
                "    clear: both\n" +
                "}\n" +
                "\n" +
                "  @page {\n" +
                "    size:  landscape;\n" +
                "\n" +
                "}\n" +
                "\n" +
                "\n" +
                "tr:nth-child(even) {\n" +
//                "  background-color: #f2f2f2;\n" +
                "  background-color: #ebf3fc;\n" +
                "}\n" +
                "\n" +
                ".table {\n" +
                "    width: 100%;\n" +
                "    margin-bottom: 20px\n" +
                "    margin-right:2\n" +
                "}\n" +
                "\n" +
                ".table th,\n" +
                ".table td {\n" +
                "    padding: 3px;\n" +
                "    line-height: 20px;\n" +
                "    text-align: center;\n" +
                "    vertical-align: top;\n" +
                "    border-top: 1px solid #ddd\n" +
                "    border: 1px solid black;\n" +
                "    font-family:sans-serif\n" +
                "    font-size: x-small;\n" +
                "}\n" +
                "\n" +
                "@page {\n" +
                "  @top-right {\n" +
                "    content: \"Page \" counter(page) \" of \" counter(pages);\n" +
                "  }\n" +
                "}\n" +
                "\n" +
                "div.footer {\n" +
                "    display: block;\n" +
                "\n" +
                "    position: running(footer);\n" +
                "}\n" +
                "\n" +
                "@page {\n" +
                "        @bottom-right {\n" +
                "            content: element(footer);\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "\n" +
                "@page {\n" +
                "  @bottom-left  {\n" +
                "\n" +
                "    content: date(\"dd.MM.yyyy HH.mm\");\n" +
                "  }\n" +
                "  }\n" +
                ".rupee{\n" +
                "    background-position:center;\n" +
                "    padding-left:15px;\n" +
                "<!--    padding-top:20px;-->\n" +
                "    width: 10px;\n" +
                "    height: 14px;\n" +
                "    background-image: url('../pdf-resources/images/rupee.png');\n" +
                "    display:block;\n" +
                "    background-repeat: no-repeat;\n" +
                "}\n" +
                ".child{\n" +
                "    display: inline-block;\n" +
                "    padding: 1rem 1rem;\n" +
                "    vertical-align: middle;\n" +
                "}\n" +
                "\n" +
                ".about-border {\n" +
                "    display: block;\n" +
                "    width: 210px;\n" +
                "    height: 1px;\n" +
                "    background: #000000;\n" +
                "\n" +
                "}\n" +
                "\n" +
                " .subHeading {\n" +
                "        display: block;\n" +
                "        font-size: 1.9em;\n" +
                "        margin-top: 0.83em;\n" +
                "        margin-bottom: 0.83em;\n" +
                "        margin-left: 0;\n" +
                "        margin-right: 0;\n" +
                "        font-weight: bold;\n" +
                "    }\n" +
                "\n" +
                "\n" +
                "\n" +
                "div.header2 {\n" +
                "    position: running(header2);\n" +
                "  }\n" +
                "\n" +
                "@page {\n" +
                " @top-center {\n" +
                "    content: element(header2);\n" +
                "    }\n" +
                " }\n" +
                "\n" +
                " @page :first {\n" +
                " @top-center {\n" +
                "    content: element(header);\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "</style>\n" +
                "</head>\n" +
                "<body>\n" +
                "<!--<div class=\"container\" >-->\n" +
                "    <div id=\"footer\"  class='footer'>\n" +
                "        <div class=\"about-border\"></div>\n" +
                "\n" +

                "<span>OIIPCRA-Tech Bid / " + formatter.format(date) + "</span> " +
                "\n" +
                "    </div>\n" +

                "<div id=\"header\" class=\"header\" >\n" +
                "    <h3><span class=\"test\" style=\"margin-left:10%; color:blue; font-family:sans-serif;\">Technical Bid Opening in Tender ID:    <strong> " + tender.getBidId() + " </strong></span>         <span class=\"test\" style=\"margin-left:2% ; color:blue; font-family:sans-serif;\"> Technical Bid Opened on Dt: <strong> " + tender.getTechBidDate() + " </strong></span></h3>\n" +


                "    <h2 style=\"text-align : center; font-family:sans-serif;text-size:15px; \"><strong>"+heading+"</strong></h2>\n" +
                "</div>\n" +
                "<div id=\"header2\" class=\"header2\" style= \"font-family:sans-serif;\"><p style=\"text-align: center;\"><strong>Odisha Community Tank Development and Management Society, Bhubaneswar</strong></p></div>\n" +
                "   <br/>\n";


        html += "    <div>\n" +
                "         <table class=\"table table-striped\" style=\"width:100%;\">\n" +
                "            <thead style=\"background-color: #457ca3; color: white;\">\n" +
                "\n" +
                "            <tr>\n" +
                "                <th style=\"width:4% ;\">Work Sl in TCN</th>\n" +
                "                <th style=\"width:8% ;\">Work ID</th>\n" +
                "                <th style=\"width:11% ;\">Amount Put to tender in Rs.</th>\n" +
                "                <th style=\"width:12% ;\">Name of Agency</th>\n" +
                "                <th style=\"width:10% ;\">Class of License</th>\n" +
                "                <th style=\"width:12% ;\">License Validity</th>\n" +
                "                <th style=\"width:8% ;\">EMD Deposited in Rs</th>\n" +
                "                <th style=\"width:7% ;\">Max Turnover in Lakh Rs.</th>\n" +
                "                <th style=\"width:1% ;\">Credit Availability in Lakh Rs.</th>\n" +
                "                <th style=\"width:6% ;\">Financial  validity  /  work  Experience</th>\n" +
                "                <th style=\"width:8% ;\">Overall Bid Validity</th>\n" +
                "\n" +
                "            </tr>\n" +
                "            </thead>\n" +
                "            <tbody>\n  ";


        List<FinancialBidopeningAbstractDto> tableDate = tenderRepositoryImpl.getTechnicalBidOpeningResult(bidId, bidOpeningDate);
        for (FinancialBidopeningAbstractDto data1 : tableDate) {
            String turn = "0.00";
            Double creditAvailability = 0.00;
            if(data1.getAgency() != null) {
                if (data1.getAgency().contains("&")) {
                    data1.setAgency(data1.getAgency().replace("&", "&amp;"));
                }
            }
//            if(data1.getTurnOver() != null){
//                turn = data1.getTurnOver();
//            }else {
//                turn = "0.00";
//            }
            if(data1.getTurnOver() == null){
                data1.setTurnOver("0.00");
            }

            if(data1.getValidity() == null){
                data1.setValidity(" ");
            }

            if(data1.getSlNo() != null){
                 data1.setSlNo(data1.getSlNo());
            }else {
                data1.setSlNo(" ");
            }

            if(data1.getWorkId() != null){
                data1.setWorkId(data1.getWorkId());
            }else {
                data1.setWorkId(" ");
            }

            if(data1.getTenderAmountString() != null){
                data1.setTenderAmountString(data1.getTenderAmountString());
            }else {
                data1.setTenderAmountString(" ");
            }

            if(data1.getEmdDepositedString() != null){
                data1.setEmdDepositedString(data1.getEmdDepositedString());
            }else {
                data1.setEmdDepositedString(" ");
            }

            if(data1.getAgency() != null){
                data1.setAgency(data1.getAgency());
            }else {
                data1.setAgency(" ");
            }

            if(data1.getLicenseClass() != null){
                data1.setLicenseClass(data1.getLicenseClass());
            }else {
                data1.setLicenseClass(" ");
            }

            if(data1.getCreditAvailability() != null){
                creditAvailability = data1.getCreditAvailability();
            }else {
                creditAvailability =0.00;
            }

            html += "<tr>" +
                    "                <td> " + data1.getSlNo() + " </td>\n" +
                    "                <td>   " + data1.getWorkId() + " </td>\n" +

                    "                <td align=\"right\"><img src=\"../pdf-resources/images/rupee.png\" width=\"8px\"/><span> " + data1.getTenderAmountString() + "</span></td>\n" +
                    "                <td style = \"text-align:left\"  >" + data1.getAgency() + "</td>\n" +
                    "                <td style = \"text-align:left\"  >" + data1.getLicenseClass() + "</td>\n" +
                    "                <td>" + data1.getValidity() + "</td>\n" +
                    "                <td align=\"right\"><img src=\"../pdf-resources/images/rupee.png\" width=\"8px\"/><span> " + data1.getEmdDepositedString() + "</span></td>\n" +
                    "                <td style = \"text-align:right\"><img src=\"../pdf-resources/images/rupee.png\" width=\"8px\"/>" + data1.getTurnOver() + "</td>\n" +
                    "                <td style = \"text-align:right\"><img src=\"../pdf-resources/images/rupee.png\" width=\"8px\"/>" + creditAvailability + "</td>\n" +
                    "                <td>" + data1.getFinancialValidityWork() + "</td>\n";

            if (data1.getOverallBidValidity().trim().equals("Not Eligible")) {
                html += "     <td style = \"text-align:left\"  ><strong>  " + data1.getOverallBidValidity() + "</strong></td>\n" +

                        "  </tr>\n";
            } else {
                html += "     <td style = \"text-align:left\"  > " + data1.getOverallBidValidity() + "</td>\n" +

                        "  </tr>\n";
            }

        }
        html += " </tbody>\n" +
                "        </table>\n" +
                "        <br/>\n" +
                "        <hr/>\n";

//        if (user.getUserLevelId() == 1) {
//            distId = 0;
//        }
//        List<Integer> disIds = null;
//        if (user.getUserLevelId() == 2) {
//            disIds = officeDataService.getDistId(userId);
//        }
//        if (user.getUserLevelId() == 6) {
//            divisionId = officeDataRepositoryImpl.getDivisionIdByUserId(userId);
//        }
//
//        List<OfficeDataDto> officeData = officeDataService.getOfficeDataDetails(distId, divisionId, userId, disIds);
//        List<OfficeDataDto> officeData = officeDataRepositoryImpl.getOfficeData(distId, divisionId, userId);
//        List<Integer> distIds = new ArrayList<>();
//        List<Integer> divisionIds = new ArrayList<>();
//        if(distIds!=null && distIds.size() > 0){
//            distIds = officeDataRepositoryImpl.getdistIdsByUserId(userId);
//        }
//        if(divisionIds!=null && divisionIds.size() > 0){
//            divisionIds = officeDataRepositoryImpl.getdivisionIdsByUserId(userId);
//        }
        List<FinancialBidopeningAbstractDto> tableDate2 = tenderRepositoryImpl.getTechnicalBidOpeningResult(bidId, bidOpeningDate);
        int length = tableDate2.toArray().length;
        for(OfficeDataDto ofData : officeData){
        if (length <= 1) {
            if(user.getRoleId() <= 4){
                html += "<p><strong>Summary record :</strong> (<strong> " + length + "</strong><strong>  <span> detail record </span></strong>)</p>\n" +
                        "        <p class=\"test\" style=\"margin-left:40%\" ><span class=\"test\" style=\"margin-left:30%\"><strong>"+ofData.getDesignationName()+"</strong></span></p>\n" +
                        "\n" +

                        "        <br/>\n" +

                        "    </div>\n";
            } else {
                html += "<p><strong>Summary record :</strong> (<strong> " + length + "</strong><strong>  <span> detail record </span></strong>)</p>\n" +
                        "        <p class=\"test\" style=\"margin-left:40%\" ><span class=\"test\" style=\"margin-left:30%\"><strong>"+ofData.getDesignationName()+", ("+ofData.getDivisionName()+")</strong></span></p>\n" +
                        "\n" +

                        "        <br/>\n" +

                        "    </div>\n";
            }
        } else {
            if(user.getRoleId() <= 4){
                html += "<p><strong>Summary records :</strong> (<strong> " + length + "</strong><strong>  <span> detail records </span></strong>)</p>\n" +
                        "        <p class=\"test\" style=\"margin-left:40%\" ><strong></strong> <span class=\"test\" style=\"margin-left:30%\"><strong>"+ofData.getDesignationName()+"</strong></span></p>\n" +
                        "\n" +
                        "<!--        <p class=\"test\" style=\"margin-left:50%\"><strong>OIIPCRA-Fin Bid/</strong>        -->\n" +
                        "        <br/>\n" +
                        "<!--        <p th:text=\"${#dates.format(standardDate, 'dd MMM yyyy')}\"></p>-->\n" +
                        "    </div>\n";
            } else {
                html += "<p><strong>Summary records :</strong> (<strong> " + length + "</strong><strong>  <span> detail records </span></strong>)</p>\n" +
                        "        <p class=\"test\" style=\"margin-left:40%\" ><strong></strong> <span class=\"test\" style=\"margin-left:30%\"><strong>"+ofData.getDesignationName()+", ("+ofData.getDivisionName()+")</strong></span></p>\n" +
                        "\n" +
                        "<!--        <p class=\"test\" style=\"margin-left:50%\"><strong>OIIPCRA-Fin Bid/</strong>        -->\n" +
                        "        <br/>\n" +
                        "<!--        <p th:text=\"${#dates.format(standardDate, 'dd MMM yyyy')}\"></p>-->\n" +
                        "    </div>\n";
            }
        }
        }
        html += "\n" +
                "\n" +
                "</body>\n" +
                "</html>\n ";


        // context.setVariable("OfficeData", officeDataService.getOfficeData(distId, divisionId, userId));


//        context.setVariable("name", name);
//        //String html = templateEngine.process("technical-bid-opening-result", context);
//        String pdfName = "technical-bid-opening-result";
        String pdfName = "technical-bid-opening-result";
        return renderPdf(html, pdfName);
    }


    @Override
    public File disbursementAndProjection(HttpServletResponse exportResponse) throws Exception {
        Context context = new Context();
        context.setVariable("DisbursementAndProjection", tenderRepositoryImpl.disbursementAndProjection());
        context.setVariable("standardDate", new Date());
        String html = templateEngine.process("disbursement-projection", context);
        String pdfName = "disbursement-projection";

        return renderPdf(html, pdfName);
    }

    @Override
    public File getListOfBids(HttpServletResponse exportResponse, Integer bidId, java.sql.Date tenderOpeningDate, Integer userId) throws IOException, Exception {
        Context context = new Context();
        List<ListOfBidsDto> finalData=new ArrayList<ListOfBidsDto>();
        List<ListOfBidsDto> listOfdata=tenderRepositoryImpl.getArrayListOfBids(bidId, tenderOpeningDate, userId);
        List<AgencyBidListPDFDTO> allList = new ArrayList<>();
        String heading = " ";
        UserInfoDto user = userQueryRepository.getUserById(userId);
        Integer distId=null;
        if (user.getUserLevelId() == 1) {
            distId = 0;
        }
        List<Integer> disIds = null;
        if (user.getUserLevelId() == 2) {
            disIds = officeDataService.getDistId(userId);
        }
        Integer divisionId=null;
        if (user.getUserLevelId() == 6) {
            divisionId = officeDataRepositoryImpl.getDivisionIdByUserId(userId);
        }

        List<OfficeDataDto> officeData = officeDataService.getOfficeDataDetails(distId, divisionId, userId, disIds);

        if(user.getUserLevelId() ==1)
        {
            heading =  "COMMUNITY TANK DEVELOPMENT AND MANAGEMENT SOCIETY, BHUBANESWAR";
        }else {
            heading =   "Minor Irrigation Division , "+officeData.get(0).getDivisionName()+" ";
        }

        SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy");
        Date date = new Date();
        String html = "<!DOCTYPE HTML>\n" +
                "<html xmlns:th=\"http://www.thymeleaf.org\" xmlns:background-color=\"http://www.w3.org/1999/xhtml\">\n" +
                "\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\"/>\n" +
                "    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\"/>\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"/>\n" +
                "    <title>Document</title>\n" +
                "    <style>\n" +
                "     .container {\n" +
                "                    padding-top: 22px;\n" +
                "                     width: 1000px;\n" +
                "                      width: auto;\n" +
                "                      margin-right: auto;\n" +

                "            margin-left: auto;\n" +
                "            *zoom: 1;\n" +
                "                }\n" +
                "                .container:before,\n" +
                "        .container:after {\n" +
                "            display: table;\n" +
                "            line-height: 0;\n" +
                "            content: \"\"\n" +
                "        }\n" +
                "\n" +
                "        .container:after {\n" +
                "            clear: both\n" +
                "        }\n" +
                "\n" +
                "p{\n" +
                "font-size:14px;\n" +
                "}\n" +
                "\n" +
                "@page { size: A4 landscape;}\n" +
                "   .container{page-break-after:always}\n" +
                "\n" +
                " @page {\n" +
                "  @bottom-right {\n" +
                "    content: \"Page \" counter(page) \" of \" counter(pages);\n" +
                "  }\n" +
                "}\n" +
                "\n" +
                //for heading box
                ".div1 {\n" +
                "  width: 1000px;\n" +
                "  height: 25px;\n" +
                "  border: 1px solid black;\n" +
                "  margin-left: 5%;"+
                "}"+
                "    </style>\n" +
                "</head>\n" +
                "<body>";



        for (ListOfBidsDto list: listOfdata) {
            List<ListOfBidsDto> listOfBids=tenderRepositoryImpl.getListOfBids(bidId, tenderOpeningDate, list.getAgencyId(), userId);

            ListOfBidsDto sum=tenderRepositoryImpl.getListOfBidSum(bidId, tenderOpeningDate, list.getAgencyId(), userId);
            if (listOfBids.get(0).getAgency().contains("&")) {
                listOfBids.get(0).setAgency(listOfBids.get(0).getAgency().replace("&", "&amp;"));
            }
            if (listOfBids.get(0).getMaxTurnOver() == null) {
                listOfBids.get(0).setMaxTurnOver(0.00);
            }

            if (listOfBids.get(0).getContact() == null) {
                listOfBids.get(0).setContact(" ");
            }



                html += "<div class=\"container\" >"+
                        "<div class=\"div1\" style=\"font-family:sans-serif; text-align:center;\" ><strong>"+heading+"</strong></div>"+
//                        "<table width=\"100%\">\n" +
//                        "        <tr>\n" +
//                        "            <td style=\"text-align:left;\"><img  src=\"../pdf-resources/images/logo.png\"  /></td>\n" +
//                        "            <td style=\"text-align:center;\"><img  src=\"../pdf-resources/images/logo-text.png\" /></td>\n" +
//                        "            <td style=\"text-align:right;\" ><img  src=\"../pdf-resources/images/govtp-logo.png\" /></td>\n" +
//                        "        </tr>\n" +
//                        "    </table>"+
                        "<br/>";
                html += "<div>\n" +
                        "        <table>\n" +
                        "\n" +
                        "            <tr>\n" +
                        "                <td><strong>LIST OF BIDS PARTICIPATED:</strong> </td>\n" +
                        "            </tr>\n" +
                        "            <hr/>\n" +
                        "            <tr>\n" +
                        "                <td> Name of Agency: &nbsp; <strong>M/S. "+listOfBids.get(0).getAgency()+", "+listOfBids.get(0).getLicenseClass()+ "</strong><strong> Contrcator</strong> </td>\n" +
                        "\n" +
                        "            </tr>\n" +
                        "            <tr>\n" +
                        "                <td>GSTIN of Agency: &nbsp; <strong>"+listOfBids.get(0).getGstInNo()+"</strong> </td>\n" +
                        "                <td>BID Identofication: &nbsp; <strong>RFB-</strong> <strong>"+listOfBids.get(0).getTenderBidId()+"</strong></td>\n" +
                        "            </tr>\n" +
                        "            <tr>\n" +
                        "                <td>Contact No: &nbsp;<strong>"+listOfBids.get(0).getContact()+"</strong></td>\n" +
                        "                <td>Date of Bid Opening: &nbsp;<strong>"+listOfBids.get(0).getCharTenderOpeningDate()+"</strong></td>\n" +
                        "            </tr>\n" +
                        "            <tr>\n" +
                        "                <td>License Expiring: &nbsp; <strong>"+listOfBids.get(0).getCharLicenseExpiring()+"</strong></td>\n" +
                        "                <td>Max Financial Turnover Rs. &nbsp; <strong><img src=\"../pdf-resources/images/rupee.png\" width=\"8px\"/>"+listOfBids.get(0).getMaxTurnOver()+"</strong> Lakh.</td>\n" +
                        "            </tr>\n" +
                        "\n" +
                        "        </table>\n" +
                        "</div>"+ //list bid participate div end
                        " <p style=\"float: right;\">Amount in Lakh INR.</p>\n" +
                        "    <br/>\n" +
                        "    <br/>\n" +
                        "    <hr/>";
                html += " <table>\n" +
                        "            <thead>\n" +
                        "            <tr>\n" +
                        "                <th scope=\"col\">Package ID</th>\n" +
                        "                <th scope=\"col\">BID Validity</th>\n" +
                        "                <th scope=\"col\">Amount put to tender (Rs.)</th>\n" +
                        "                <th scope=\"col\">Amount of Bid by Agency (Rs.)</th>\n" +
                        "                <th scope=\"col\">Bid % Excess/Less</th>\n" +
                        "                <th scope=\"col\">Similar Work Agmt Required</th>\n" +
                        "                <th scope=\"col\">Annual turnover Required</th>\n" +
                        "                <th scope=\"col\">Credit facility Required</th>\n" +
                        "                <th scope=\"col\">Similar Work Done</th>\n" +
                        "                <th scope=\"col\">Credit Facility Available</th>\n" +
                        "                <th scope=\"col\">Max Annual Turnover</th>\n" +
                        "            </tr>\n" +
                        "            </thead>\n" +
                        "\n" +


                        "            <tbody>\n" ;

            for (ListOfBidsDto data:listOfBids) {
                if(data.getCreditFacilityRequired()==null){
                  data.setCreditFacilityRequired(0.00);
                }
                if(data.getCharAmountQuoted()!=null) {
                    if ( data.getCharAmountQuoted().trim().contains(".00")) {
                        data.setCharAmountQuoted("0.00");
                    }
                } else {
                  data.setCharAmountQuoted("0.00");
                }
                if(data.getAmountPercentage()==null){
                    data.setAmountPercentage(0.00);
                }
                if(data.getMaxTurnOver()==null){
                    data.setMaxTurnOver(0.00);
                }
                if(data.getCreditFacilityAvailable()==null){
                    data.setCreditFacilityAvailable(0.00);
                }
                if(data.getPackageId()==null){
                    data.setPackageId(" ");
                }
                if(data.getCharTenderAmount()==null){
                    data.setCharTenderAmount("0.00");
                }
                if(data.getSimilarWorkValue()==null){
                    data.setSimilarWorkValue(0.00);
                }
                if(data.getCharAnnualTurnover()==null){
                    data.setCharAnnualTurnover("0.00");
                }

                html +=  "            <tr>\n" +
                        "\n" +
                        " <td>" + data.getPackageId() + "</td>\n" +
                        "            <td>" + data.getBidValidity() + "</td>\n" +
                        "            <td><img src=\"../pdf-resources/images/rupee.png\" width=\"8px\"/>" + data.getCharTenderAmount() + "</td>\n" +
                        "            <td><img src=\"../pdf-resources/images/rupee.png\" width=\"8px\"/>" + data.getCharAmountQuoted() + "</td>\n" +
                        "            <td style=\"width: 10%\">" + data.getAmountPercentage() + " %</td>\n" +
                        "            <td><img src=\"../pdf-resources/images/rupee.png\" width=\"8px\"/>" + data.getSimilarWorkValue() + "</td>\n" +
                        "            <td><img src=\"../pdf-resources/images/rupee.png\" width=\"8px\"/>" + data.getCharAnnualTurnover() + "</td>\n" +
                        "            <td><img src=\"../pdf-resources/images/rupee.png\" width=\"8px\"/>" + data.getCreditFacilityRequired() + "</td>\n" +
                        "            <td><img src=\"../pdf-resources/images/rupee.png\" width=\"8px\"/>" + data.getSimilarWorkValue() + "</td>\n" +
                        "            <td><img src=\"../pdf-resources/images/rupee.png\" width=\"8px\"/>" + data.getCreditFacilityAvailable() + "</td>\n" +
                        "            <td><img src=\"../pdf-resources/images/rupee.png\" width=\"8px\"/>" + data.getMaxTurnOver() + "</td>\n" +
                        "\n" +
                        "            </tr>\n" +
                        "\n" ;
                         }
            if(sum.getCharAmountQuoted()!=null) {
                if (sum.getCharAmountQuoted().trim().contains(".00")) {
                    sum.setCharAmountQuoted("0.00");
                }
            }
                html += "<hr style = \"width:1750%\" />"+
                        "            <tr>\n" +

                        "\n" +
                        "            <td>"+ "  "+"</td>\n" +
                        "            <td>" + "Total:" + "</td>\n" ;
            Double sumTenderAmt = 0.0;
            Double sumAmtQuoted = 0.0;
            Double sumAnnualTurnover = 0.0;
            Double CredReq = 0.0;
            Double CredAvail = 0.0;

            String sumTender=" " ;
            String sumQuoted=" " ;
            String cred = " ";
            String annual = " ";
            String reqValue = " ";

            for (int i = 0; i < listOfBids.size(); i++) {
                if(listOfBids.get(i).getCharAmountQuoted() != null)
                    if(listOfBids.get(i).getCharTenderAmount() != null) {
                        sumTenderAmt += Double.valueOf((listOfBids.get(i).getCharTenderAmount().trim()).replace(",", ""));
                        sumTender = tenderRepositoryImpl.getValue(sumTenderAmt);
//                        html += "            <td><img src=\"../pdf-resources/images/rupee.png\" width=\"8px\"/>" + sumTender + "</td>\n";
                    }
                if(listOfBids.get(i).getCharAmountQuoted() != null) {
                    sumAmtQuoted += Double.valueOf((listOfBids.get(i).getCharAmountQuoted().trim()).replace(",", ""));
                    sumQuoted = tenderRepositoryImpl.getValue(sumAmtQuoted);
//                    html += "            <td><img src=\"../pdf-resources/images/rupee.png\" width=\"8px\"/>" + sumQuoted + "</td>\n";
                }
//                html += "            <td>" + "   " + "</td>\n" +
//                        "            <td>" + "   " + "</td>\n" ;
                if(listOfBids.get(i).getCharAnnualTurnover() != null) {
                    sumAnnualTurnover += Double.valueOf((listOfBids.get(i).getCharAnnualTurnover().trim()).replace(",", ""));
                    annual = tenderRepositoryImpl.getValue(sumAnnualTurnover);

//                    html += "            <td><img src=\"../pdf-resources/images/rupee.png\" width=\"8px\"/>" + sumAnnualTurnover + "</td>\n";
                }
                if(listOfBids.get(i).getCreditFacilityRequired() != null) {
//                    CredReq += Double.valueOf((listOfBids.get(i).getCharCreditFacilityRequired().trim()).replace(",", ""));
                      CredReq += Double.valueOf((listOfBids.get(i).getCreditFacilityRequired()));
                      reqValue = tenderRepositoryImpl.getValue(CredReq);

//                    html += "            <td><img src=\"../pdf-resources/images/rupee.png\" width=\"8px\"/>" + CredReq + "</td>\n" ;
                }
//                html += "            <td>" + " " + "</td>\n";
                if(listOfBids.get(i).getCreditFacilityAvailable() != null) {
                    CredAvail += Double.valueOf((listOfBids.get(i).getCreditFacilityAvailable()));
                    cred = tenderRepositoryImpl.getValue(CredAvail);
//                    html += "            <td><img src=\"../pdf-resources/images/rupee.png\" width=\"8px\"/>" + cred + "</td>\n";
                }
            }
            html += " <td><img src=\"../pdf-resources/images/rupee.png\" width=\"8px\"/>" + sumTender + "</td>\n" +
                    "<td><img src=\"../pdf-resources/images/rupee.png\" width=\"8px\"/>" + sumQuoted + "</td>\n" +
                    "<td></td>" +
                    "<td></td>" +
                    "<td><img src=\"../pdf-resources/images/rupee.png\" width=\"8px\"/>" + annual + "</td>\n" +
                    "<td><img src=\"../pdf-resources/images/rupee.png\" width=\"8px\"/>" + reqValue + "</td>\n" +
                    "<td></td>" +
                    "<td><img src=\"../pdf-resources/images/rupee.png\" width=\"8px\"/>" + cred + "</td>\n ";

                        html += "            <td>" +" " + "</td>\n" +
                        "\n" +
                        "            </tr>\n" +
                        "\n" +
                        "            </tbody>\n" +
                        "        </table>"+
                       // "    <p><b>Total:</b></p>\n" +
                        "    <hr/>\n" +
                        "    <h4><strong> Summary for Agency: &nbsp; ("+listOfBids.size()+" detail records) </strong></h4>\n" +
                        "    <hr/>\n" +
                        "\n" +
                        "\n" +
                        "    <br/>\n" +
                        "    <p> Date: &nbsp;"+formatter.format(date)+"</p>\n" ;


            for(OfficeDataDto ofData : officeData) {
                if(user.getRoleId() <= 4){
                    html += "    <p style=\"float: right;\">"+ofData.getDesignationName()+"</p>" +
                            "</div>";
                } else {
                    html += "    <p style=\"float: right;\">"+ofData.getDesignationName()+", ("+ofData.getDivisionName()+")</p>" +
                            "</div>";//container div end
                }
            }
        }
        html +=

                    ""+
                "</body>\n" +
                "</html>";

        String pdfName = "GetListOfBids";
        return renderPdf(html, pdfName);
//        context.setVariable("ListOfBids", tenderRepositoryImpl.getListOfBids(bidId, tenderOpeningDate));
//        String html = templateEngine.process("list-of-bids", context);
    }



    @Override
    public File abstractOfFinancialBidOpening(HttpServletResponse exportResponse, String bidId, Date bidOpeningDate) throws Exception {
        Context context = new Context();
        context.setVariable("AbstractData", tenderRepositoryImpl.getAbstractOfFinancialBidOpening(bidId, bidOpeningDate));
        context.setVariable("Opening_Date", bidOpeningDate);
        String html = templateEngine.process("abstract-financial-bid-opening", context);

        String pdfName = "FinancialBidResult";
        return renderPdf(html, pdfName);
    }
    @Override
    public File getPackageWiseBidders(HttpServletResponse exportResponse, Integer bidId, java.sql.Date techBidDate, Integer agencyId, Integer userId) throws IOException, Exception {
        Context context = new Context();
        context.setVariable("packageWiseBidderData", tenderRepositoryImpl.getPackageWiseBidders(bidId, techBidDate, agencyId, userId));
//        context.setVariable("Opening_Date", bidOpeningDate);
        String html = templateEngine.process("package-wise-bidders", context);
        String pdfName = "PackageWiseBidders";
        return renderPdf(html, pdfName);
    }

    @Override
    public File getTechnicalBids(HttpServletResponse exportResponse, Integer bidId, java.sql.Date techBidDate, Integer packageId, Integer userId) throws IOException, Exception {
        Context context = new Context();
        context.setVariable("technicalBidsData", tenderRepositoryImpl.getTechnicalBids(bidId, techBidDate, packageId, userId));
        context.setVariable("eProcurementNotice1", tenderRepositoryImpl.eProcurementNotice1(bidId));
        context.setVariable("eProcurementNotice2", tenderRepositoryImpl.eProcurementNotice2(bidId));
        context.setVariable("technicalBidsLast3PagesData", tenderRepositoryImpl.getTechBidBidderDetails(bidId, techBidDate, packageId, userId));
        String html1 = templateEngine.process("technical-bid-pdf1", context);
        String pdf1 = "TechnicalBids";
        String html2 = templateEngine.process("technical-bid-pdf2", context);
        String pdf2= "TechnicalBids";

        File file1 = renderPdf(html1, pdf1);
        File file2 = renderPdf(html2, pdf2);
        List<File> files = new ArrayList<>();
        files.add(file1);
        files.add(file2);

        String mergedFileName = new ClassPathResource(PDF_RESOURCES).getFile().getPath() + "TechnicalBids.pdf";
        File file = File.createTempFile("TechnicalBids", ".pdf");
        OutputStream outputStream=new FileOutputStream(file);
        ITextRenderer renderer = new ITextRenderer(20f * 4f / 3f, 18);
        renderer.setDocumentFromString(html1, new ClassPathResource(PDF_RESOURCES).getFile().getPath());

        renderer.layout();
        renderer.createPDF(outputStream);
        outputStream.close();


        File mergedFile = new File(mergedFileName);

        PDFMergerUtility pdfMergerUtility = new PDFMergerUtility();
        pdfMergerUtility.setDestinationFileName(new ClassPathResource(PDF_RESOURCES).getFile().getPath() + "TechnicalBids.pdf");
        pdfMergerUtility.addSource(file1);
        pdfMergerUtility.addSource(file2);

        pdfMergerUtility.mergeDocuments(MemoryUsageSetting.setupMainMemoryOnly());
        return mergedFile;
    }


    @Override
    public File getTechnicalBidAttachments(HttpServletResponse exportResponse, Integer bidId, java.sql.Date techBidDate, Integer packageId, Integer userId) throws IOException, Exception {
        Context context = new Context();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        FinYrDto finYr = tenderRepositoryImpl.getFinyr(bidId,techBidDate);
        List<TechnicalBidAttachmentDto> technicalBidAttachment = tenderRepositoryImpl.getTechnicalBidAttachmentDetails(bidId,techBidDate,packageId);
        Double previousYearWeightage = tenderRepositoryImpl.getPreviousYearWeightage(bidId);
        String valueAt = " ";

        Integer difference = 0;
        String lastTwo = null;
        String value2 = finYr.getName();
        lastTwo = value2.substring(value2.length()-2);


        for(int a=0; a<technicalBidAttachment.size();a++)
        {
            String lastTwo2= null;
            String value3 = technicalBidAttachment.get(a).getFinYrName();
            lastTwo2 = value3.substring(value3.length()-2);
            difference = Integer.parseInt(lastTwo)-Integer.parseInt(lastTwo2);
            Double amount = technicalBidAttachment.get(a).getAmount();

            for(int i =1; i<=difference; i++)
            {
                amount = amount * previousYearWeightage;

            }
            valueAt = tenderRepositoryImpl.getRoundValue2(amount);
            technicalBidAttachment.get(a).setPreviousYearWeightage(valueAt);
        }

        for(int i=0; i<technicalBidAttachment.size();i++)
        {
            String value  = tenderRepositoryImpl.getValueByBidderId(technicalBidAttachment.get(i).getBidderId());
            technicalBidAttachment.get(i).setValueAt(value);
        }

        for(int j= 0; j<technicalBidAttachment.size(); j++ )
        {
            if(technicalBidAttachment.get(j).getAgreedBidValidity().equals("Yes"))
            {
                technicalBidAttachment.get(j).setEligibility("Qualified");
                technicalBidAttachment.get(j).setResponsiveOrNot("Responsive");

            } else {
                technicalBidAttachment.get(j).setEligibility("Not Qualified");
                technicalBidAttachment.get(j).setResponsiveOrNot("Non-Responsive");
            }

        }

        List<TechnicalBidAttachmentDto> similar = tenderRepositoryImpl.getSimilarWorkValue(bidId,packageId);
        String bidOpening = tenderRepositoryImpl.getBidOpeningDate(bidId);

        Integer totalRecord = technicalBidAttachment.size();
        String record = " ";
        if(totalRecord < 1)
        {
            record = totalRecord + " No. ";
        }
        else {
            record = totalRecord + " Nos.";
        }
        context.setVariable("technicalBidAttachment",technicalBidAttachment);
        context.setVariable("similar",similar);
        context.setVariable("record",record);
        context.setVariable("bidOpening",bidOpening);
        context.setVariable("finYr",finYr);

        String html1 = templateEngine.process("technical-bid-attachment-5B-pdf1", context);
        String pdf1 = "TechnicalBids";
        String html2 = templateEngine.process("technical-bid-attachment-5A-pdf2", context);
        String pdf2= "TechnicalBids";
        String html3 = templateEngine.process("technical-bid-attachment-5-pdf3", context);
        String pdf3= "TechnicalBids";

        File file1 = renderPdf(html1, pdf1);
        File file2 = renderPdf(html2, pdf2);
        File file3 = renderPdf(html3, pdf3);
        List<File> files = new ArrayList<>();
        files.add(file1);
        files.add(file2);
        files.add(file3);
        String mergedFileName = new ClassPathResource(PDF_RESOURCES).getFile().getPath() + "TechnicalBids.pdf";
        File file = File.createTempFile("TechnicalBids", ".pdf");
        OutputStream outputStream=new FileOutputStream(file);
        ITextRenderer renderer = new ITextRenderer(20f * 4f / 3f, 18);
        renderer.setDocumentFromString(html1, new ClassPathResource(PDF_RESOURCES).getFile().getPath());

        renderer.layout();
        renderer.createPDF(outputStream);
        outputStream.close();


        File mergedFile = new File(mergedFileName);

        PDFMergerUtility pdfMergerUtility = new PDFMergerUtility();
        pdfMergerUtility.setDestinationFileName(new ClassPathResource(PDF_RESOURCES).getFile().getPath() + "TechnicalBids.pdf");
        pdfMergerUtility.addSource(file1);
        pdfMergerUtility.addSource(file2);
        pdfMergerUtility.addSource(file3);

        pdfMergerUtility.mergeDocuments(MemoryUsageSetting.setupMainMemoryOnly());
        return mergedFile;
    }

    @Override
    public File getFinancialBid(HttpServletResponse exportResponse, Integer bidId, java.sql.Date techBidDate, Integer packageId, Integer userId) throws IOException, Exception {
        Context context = new Context();
//        context.setVariable("PackageWiseBidderData", tenderRepositoryImpl.getFinancialBid(bidId, userId));
//        context.setVariable("Opening_Date", bidOpeningDate);
        String html1 = templateEngine.process("financial-bid-pdf1", context);
        String pdf1 = "FinancialBid";
        String html2 = templateEngine.process("financial-bid-pdf2", context);
        String pdf2 = "FinancialBid";

        File file1 = renderPdf(html1, pdf1);
        File file2 = renderPdf(html2, pdf2);
        List<File> files = new ArrayList<>();
        files.add(file1);
        files.add(file2);

        String mergedFileName = new ClassPathResource(PDF_RESOURCES).getFile().getPath() + "FinancialBid.pdf";
        File file = File.createTempFile("FinancialBid", ".pdf");
        OutputStream outputStream=new FileOutputStream(file);
        ITextRenderer renderer = new ITextRenderer(20f * 4f / 3f, 18);
        renderer.setDocumentFromString(html1, new ClassPathResource(PDF_RESOURCES).getFile().getPath());

        renderer.layout();
        renderer.createPDF(outputStream);
        outputStream.close();

        File mergedFile = new File(mergedFileName);

        PDFMergerUtility pdfMergerUtility = new PDFMergerUtility();
        pdfMergerUtility.setDestinationFileName(new ClassPathResource(PDF_RESOURCES).getFile().getPath() + "FinancialBid.pdf");
        pdfMergerUtility.addSource(file1);
        pdfMergerUtility.addSource(file2);

        pdfMergerUtility.mergeDocuments(MemoryUsageSetting.setupMainMemoryOnly());
        return mergedFile;
    }



    @Override
    public File formG(HttpServletResponse exportResponse, Integer finYrId, String issueNo, Integer agencyId, Integer distId, Integer divisionId, Integer userId) throws Exception {
        Context context = new Context();
        Map<String, Object> result = new HashMap();
        String s = "";
        List<FormGDto> finalData = new ArrayList();
        FormGDto value = tenderRepositoryImpl.getSum(finYrId, issueNo, agencyId);
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy ");
        Date date = new Date();


//        context.setVariable("value",tenderRepositoryImpl.getSum(finYrId, issueNo, agencyId));
//        context.setVariable("agency",tenderRepositoryImpl.getAgencyDetails(finYrId, issueNo, agencyId));
//        context.setVariable("formG",tenderRepositoryImpl.getFormGDetails(finYrId, issueNo, agencyId));
//        context.setVariable("issueNo", issueNo);
//        context.setVariable("OfficeData", officeDataService.getOfficeData(distId,divisionId,userId));
        //context.setVariable("Opening_Date", bidOpeningDate);
        //String html = templateEngine.process("formG", context);
        String html = "";

        html += "<!DOCTYPE HTML>\n" +
                "<html>\n" +

                "<head>\n" +
                "    <meta charset=\"UTF-8\"/>\n" +
                "    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\"/>\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"/>\n" +
                "    <title>Document</title>\n" +
                "    <style>\n" +
                "        body {\n" +
                "    text-align: justify;\n" +
                "    text-justify: inter-word;\n" +
                "    font-family: Arial, Helvetica, sans-serif;\n" +
                "}\n" +
                "\n" +
                ".container {\n" +
                "    padding-top: 22px;\n" +
                "    width: 1000px;\n" +
                "    width: auto;\n" +
                "    margin-right: auto;\n" +
                "    margin-left: auto;\n" +
                "    *zoom: 1;\n" +
                "}\n" +
                ".container:before,\n" +
                ".container:after {\n" +
                "    display: table;\n" +
                "    line-height: 0;\n" +
                "    content: \"\";\n" +
                "}\n" +
                "\n" +
                ".container:after {\n" +
                "    clear: both;\n" +
                "}\n" +
                "div.page1 {\n" +
                "    page-break-inside:avoid; \n" +
                "} " +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "}\n" +
                "\n" +
                "div.footer {\n" +
                "    display: block;\n" +
                "\n" +
                "    position: running(footer);\n" +
                "}\n" +
                "\n" +
                "@page {\n" +
                "   @bottom-left {\n" +
                "        content:  element(footer);\n" +
                "    }\n" +
                "  }\n" +

                "\n" +
                "  @page {\n" +
                "    size: A4 landscape;\n" +
                "}\n" +
                "\n" +
                ".padding {\n" +
                "    padding-right: 20px;\n" +
                "    width: 34%;\n" +
                "}\n" +
                ".paddingRight {\n" +
                "    padding-left: 10px;\n" +
                "    padding-right: 10px;\n" +
                "}\n" +
                "\n" +
                ".test{\n" +
                "align :right ;\n" +
                "}\n" +
                "\n" +
                "\n" +
                ".about-border {\n" +
                "    display: block;\n" +
                "    width: 160px;\n" +
                "    height: 1px;\n" +
                "    background: #000000;\n" +
                "\n" +
                "}\n" +
                ".mainTable {\n" +
                "\n" +
                "    border-spacing: 0px;\n" +
                "\n" +
                "}\n" +
                "td {\n" +
                "    border: none;\n" +
                "}\n" +
                ".mainTableTr {\n" +
                "  border-bottom: 1pt solid black;\n" +
                "}\n" +
                "table thead th { border-bottom: 1px solid #000; }\n" +
                "\n" +
                ".box {\n" +
                "\n" +
                "  padding: 10px;\n" +
                "  border: 2px solid black;\n" +
                "  box-shadow: 5px 5px #888888;\n" +
                "\n" +
                "}\n" +
                "\n" +
                "</style>\n" +
                "</head>\n" +
                "<body>\n" +
                "\n";

        List<OfficeDataDto> officeData = officeDataService.getOfficeData(distId, divisionId, userId);
        List<AgencyDto> agency = tenderRepositoryImpl.getAgencyData(finYrId, issueNo, agencyId);
        for (AgencyDto data : agency) {
if(data.getName().contains("&")){
    data.setName(data.getName().replace("&","&amp;"));
}

            html += "    <div class=\"page1\">\n" +
                    "          <table width=\"100%\" style=\"border-style: none;\">\n" +
                    "            <tr>\n" +
                    "                <td style=\"text-align:left;border-style: none;\"><img src=\"../pdf-resources/images/govtp-logo.png\"/></td>\n" +
                    "                <td style=\"text-align:center;border-style: none;\">\n" +
                    "                    <span class=\"box\" ><strong> ODISHA COMMUNITY TANK DEVELOPMENT AND MANAGEMENT SOCIETY, BHUBANESWAR</strong></span> <br/><br/>\n" +
                    "\n" +
                    "                    <strong> " + officeData.get(0).getSpuAddress() + "  " + officeData.get(0).getSpuPost() + "  . " + officeData.get(0).getSpuPinNo() + " Ph: " + officeData.get(0).getSpuPinNo() + " ,E-Mail:  " + officeData.get(0).getSpuEmail() + "  </strong> <br/>" +
                    "\n" +
                    "\n" +
                    "\n" +
                    "                </td>\n" +
                    "                <td style=\"text-align:right;border-style: none;\"><img src=\"../pdf-resources/images/logo.png\"/>\n" +
                    "                </td>\n" +
                    "            </tr>\n" +
                    "        </table>\n";


            html += "\n" +
                    "\n" +
                    "        <p><strong>Form-G :</strong>  <strong>Activity Report of Contractors</strong></p>\n" +
                    "        <p>Financial Year : <strong> " + data.getFinyr() + "</strong>         <span class=\"test\" style=\"margin-left:22%\">No./Date of Issue No. <strong>" + issueNo + " / Dt:      " + formatter.format(date) + " </strong></span></p>\n" +
                    "        <p> Name of Agency : <strong> M/s.</strong> <strong> " + data.getName() + " </strong>    <span class=\"test\" style=\"margin-left:10%\">GSTIN of Agency: <strong> " + data.getGstinNo() + "  </strong></span>   <span class=\"test\" style=\"margin-left:15%\"><strong> " + data.getGstinNo() + " </strong></span></p>\n" +
                    "        <p><span class=\"test\" style=\"margin-left:15%\"><strong> " + data.getClassOfLicense() + "   Contractor  </strong></span>  <span class=\"test\" style=\"margin-left:50%\"><strong><u>Amount in INR.</u></strong></span></p>\n";

            html += " <hr style=\" border: 1px solid black;\"/>\n" +
                    "            <table class=\"mainTable\" style=\"width:100%; border: 0px;text-align: center !important;\">\n" +
                    "            <thead>\n" +
                    "            <tr class=\"mainTableTr\">\n" +
                    "                <th>Sl.No</th>\n" +
                    "                <th>Bid ID / Name of Work / Supply / Package ID</th>\n" +
                    "                <th>District/Block</th>\n" +
                    "                <th>Scheme</th>\n" +
                    "                <th>Date Opening of Tender</th>\n" +
                    "                <th>Estimated Cost Put to tender</th>\n" +
                    "                <th>Validity of Tender</th>\n" +
                    "                <th>Awarded with if any</th>\n" +
                    "                <th>Legal Case initiated if any </th>\n" +
                    "                <th>Remarks </th>\n" +
                    "            </tr>\n" +
                    "            </thead>\n" +
                    "                <tbody>\n" +
                    "\n" +
                    "                <br/>\n";


            List<FormGDto> formG = tenderRepositoryImpl.getFormGDetails(finYrId, issueNo, data.getId());
            int i = 1;
            for (FormGDto data1 : formG) {
                String award = null;
                String dist = "";
                if (data1.getDistrict() == null) {
                    dist = "";
                } else {
                    dist = data1.getDistrict();
                }
                if (data1.getAwarded() == null) {
                    award = " ";
                } else {
                    award = data1.getAwarded();
                }
                html += "<tr class=\"mainTableTr\" >\n" +

                        "                <td>  " + i++ + " </td>\n" +
                        "                <td> " + data1.getBid_workId() + " </td>\n" +
                        "                <td> " + dist + "</td>\n" +
                        "                <td> " + data1.getScheme() + "</td>\n" +
                        "                <td> " + data1.getDateString() + "</td>\n" +
                        "                <td><img src=\"../pdf-resources/images/rupee.png\" width=\"8px\"/> " + data1.getTenderAmountString() + "</td>\n" +
                        "                <td> " + data1.getValidityOfTender() + "</td>\n" +
                        "                <td> " + award + " </td>\n" +
                        "                <td></td>\n" +
                        "                <td ></td>\n" +
                        "                </tr>\n";
            }

            html += "</tbody> " +
                    " " +
                    "</table> </div> " +
                    " " +
                    "<br/> ";

            UserInfoDto user = userQueryRepository.getUserById(userId);
            if (user.getUserLevelId() == 1) {
                distId = 0;
            }
            List<Integer> disIds = null;
            if (user.getUserLevelId() == 2) {
                disIds = officeDataService.getDistId(userId);
            }
            if (user.getUserLevelId() == 6) {
                divisionId = officeDataRepositoryImpl.getDivisionIdByUserId(userId);
            }

            List<OfficeDataDto> ofData = officeDataService.getOfficeDataDetails(distId, divisionId, userId, disIds);
            for(OfficeDataDto oData : ofData) {
                List<FormGDto> formG2 = tenderRepositoryImpl.getFormGDetails(finYrId, issueNo, data.getId());
                int length = formG2.toArray().length;
                if (length <= 1) {
                    html += "<hr  style=\" border: 1px solid black;\"/>\n" +
                            "        <p><strong>Summary for Agency :</strong> (<strong> " + length + " detail Record </strong>)         <span class=\"test\" style=\"margin-left:1%\"><strong>Total Cost of tender participated:</strong> <strong>" + formG2.get(0).getSumString() + "</strong></span></p>\n" +
                            "        <hr style=\" border: 1px solid black;\"/>\n" +
                            "        <p style=\"margin-right: 54px;margin-top: 90px; !important;text-align: right;justify-items: center;\">"+oData.getDesignationName()+"</p>\n ";


                } else {

                    html += "<hr  style=\" border: 1px solid black;\"/>\n" +
                            "        <p><strong>Summary for Agency :</strong> (<strong> " + length + " detail Records </strong>)         <span class=\"test\" style=\"margin-left:1%\"><strong>Total Cost of tender participated:</strong> <strong>" + formG2.get(0).getSumString() + "</strong></span></p>\n" +
                            "        <hr style=\" border: 1px solid black;\"/>\n" +
                            "        <p style=\"margin-right: 54px;margin-top: 90px; !important;text-align: right;justify-items: center;\">"+oData.getDesignationName()+"</p>\n ";


                }
            }
        }
        html += "</body> </html> ";


        String pdfName = "formG";
        return renderPdf(html, pdfName);
    }


    @Override
    public File draftTenderNotice(HttpServletResponse response, Integer bidId, String bidOpeningDate,
                                  Integer distId, Integer divisionId, Integer userId, String issueNo,
                                  String issueDate, List<Integer> distIds) throws Exception {
        Context context = new Context();
        DraftTenderNoticeDto draftTenderNoticeDto = tenderRepositoryImpl.getDraftTenderNotice(bidId, bidOpeningDate);

        String issueNo2 = issueNo;
        int i = Integer.parseInt(issueNo2);
        i++;


        draftTenderNoticeDto.setMemoNumber(i);

        BigDecimal amount = draftTenderNoticeDto.getVariesTo();
        draftTenderNoticeDto.setVariesToString(String.format("%.2f", amount));

        BigDecimal amount2 = draftTenderNoticeDto.getVariesFrom();
        draftTenderNoticeDto.setVariesFromString(String.format("%.2f", amount2));

        BigDecimal amount3 = draftTenderNoticeDto.getTenderAmount();
        draftTenderNoticeDto.setTenderAmountString(String.format("%.2f", amount3));


        Date input = draftTenderNoticeDto.getPreBidMeetingDate();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(input);
        cal1.add(Calendar.DAY_OF_YEAR, -1);
        Date previousDate = cal1.getTime();
        draftTenderNoticeDto.setPreviousDate(previousDate);

//        double tenderAmount = draftTenderNoticeDto.getTenderAmount();
//        long lng = (long) tenderAmount;
//        String amt = noToWord(lng);
//        draftTenderNoticeDto.setTenderAmountString(amt);
        context.setVariable("DraftTenderNotice", draftTenderNoticeDto);
        context.setVariable("Opening_Date", bidOpeningDate);
        UserInfoDto user = userQueryRepository.getUserById(userId);
        if (user.getUserLevelId() == 1) {
            distId = 0;
        }
        List<Integer> disIds = null;
        if (user.getUserLevelId() == 2) {
            disIds = officeDataService.getDistId(userId);
        }
        if (user.getUserLevelId() == 6) {
            // distId = officeDataService.getDistId(userId);
            divisionId = officeDataRepositoryImpl.getDivisionIdByUserId(userId);
        }

        List<OfficeDataDto> officeData = officeDataService.getOfficeDataDetails(distId, divisionId, userId, disIds);
        for(OfficeDataDto od:officeData){
            if(od.getDivisionName()!= null){
                od.setDivisionName(", "+ od.getDivisionName());
            }
            else{
                od.setDivisionName(" ");
            }
        }
        if(user.getUserLevelId() == 1){
            if(officeData.size()>0){
                officeData.get(0).setDistrictName("Khordha");
            }
        }

        context.setVariable("OfficeData", officeData);

        List<String> nameOfTheMip = new ArrayList<>();
        List<DraftNoticeAnnexureADto> annexureData = tenderRepositoryImpl.getDraftNoticeAnnexureA(bidId);
      /*  for (DraftNoticeAnnexureADto data1 : annexureData) {

            if (data1.getNameOfTheMip() == null) {
                nameOfTheMip.add(" ");
            } else {
                nameOfTheMip.add(data1.getNameOfTheMip());
            }
        }*/
        for (int j = 0; j < annexureData.size(); j++) {

            if (annexureData.get(j).getNameOfTheMip() == null) {
                annexureData.get(j).setNameOfTheMip(" ");
            } else {
                annexureData.get(j).setNameOfTheMip(annexureData.get(j).getNameOfTheMip() + " MIP in");
            }
            if (annexureData.get(j).getBlockName() == null) {
                annexureData.get(j).setBlockName(" ");
            } else {
                annexureData.get(j).setBlockName(annexureData.get(j).getBlockName() + "   Block-");
            }
            if (annexureData.get(j).getDistName() == null) {
                annexureData.get(j).setDistName(" ");
            } else {
                annexureData.get(j).setDistName(" in " + annexureData.get(j).getDistName() + "   District");
            }
            if(annexureData.get(0).getBiddingType() == null){
               annexureData.get(0).setBiddingType(" ");
            }

        }
        List<DraftNoticeAnnexureADto> annexureAData = tenderRepositoryImpl.getDraftNoticeAnnexureA(bidId);
        for (int j = 0; j < annexureAData.size(); j++) {

            if (annexureAData.get(j).getNameOfTheMip() == null) {
                annexureAData.get(j).setNameOfTheMip(" ");
            } else {
                annexureAData.get(j).setNameOfTheMip(annexureAData.get(j).getNameOfTheMip());
            }
            if (annexureAData.get(j).getBlockName() == null) {
                annexureAData.get(j).setBlockName(" ");
            } else {
                annexureAData.get(j).setBlockName(annexureAData.get(j).getBlockName());
            }
            if (annexureAData.get(j).getDistName() == null) {
                annexureAData.get(j).setDistName(" ");
            } else {
                annexureAData.get(j).setDistName(annexureAData.get(j).getDistName());
            }

        }

        List<DraftNoticeAnnexureADto> annexureATableData = tenderRepositoryImpl.getDraftNoticeAnnexureATableData(bidId);

        if(bidId == 14 || bidId == 15 || bidId == 16 || bidId == 95) {
            for (int k = 0; k < annexureATableData.size(); k++) {
                String name = "";
                if (annexureATableData.get(k).getNameOfTheMip() != null) {
                    name += annexureATableData.get(k).getNameOfTheMip() + " MIP ";
                }
                if (annexureATableData.get(k).getBlockName() != null) {
                    name += "in " + annexureATableData.get(k).getBlockName() + " Block ";
                }
                if (annexureATableData.get(k).getNameOfWork() != null) {
                    if (annexureATableData.get(k).getNameOfWork().contains("OIIPCRA")) {
                        name += " - " + annexureATableData.get(k).getNameOfWork();
                    } else {
                        name += " - " + annexureATableData.get(k).getNameOfWork() + " (OIIPCRA)";
                    }
                }
                if (annexureATableData.get(k).getDistName() != null) {
                    name += " in " + annexureATableData.get(k).getDistName() + " District ";
                }
                annexureATableData.get(k).setNameOfWork(name);
            }
        }

        context.setVariable("data", annexureData);
        context.setVariable("annexureATableData", annexureATableData);
        context.setVariable("annexureAData", annexureAData);
        //context.setVariable("data1,annexureData");
        //context.setVariable("data1",tenderRepositoryImpl.getDraftNoticeAnnexureA(bidId));

        String html1 = templateEngine.process("draft-tender-notice-pdf1", context);
        String pdfName1 = "DraftTenderNotice";
        String html2 = templateEngine.process("draft-tender-notice-pdf2", context);
        String pdfName2 = "DraftTenderNotice";
        String html3 = templateEngine.process("draft-tender-notice-pdf3", context);
        String pdfName3 = "DraftTenderNotice";
        File file1 = renderPdf(html1, pdfName1);
        File file2 = renderPdf(html2, pdfName2);
        File file3 = renderPdf(html3, pdfName3);
        List<File> files = new ArrayList<>();
        files.add(file1);
        files.add(file2);
        files.add(file3);
        String mergedFileName = new ClassPathResource(PDF_RESOURCES).getFile().getPath() + "DraftTenderNotice.pdf";
        // mergePDFFiles(files, mergedFileName);

        File file = File.createTempFile("DraftTenderNotice", ".pdf");
        OutputStream outputStream = new FileOutputStream(file);
        ITextRenderer renderer = new ITextRenderer(20f * 4f / 3f, 18);
        renderer.setDocumentFromString(html1, new ClassPathResource(PDF_RESOURCES).getFile().getPath());
        renderer.layout();
        renderer.createPDF(outputStream);
        outputStream.close();


        File mergedFile = new File(mergedFileName);
//        mergedFile.mkdir();

        PDFMergerUtility pdfMergerUtility = new PDFMergerUtility();
        pdfMergerUtility.setDestinationFileName(new ClassPathResource(PDF_RESOURCES).getFile().getPath() + "DraftTenderNotice.pdf");
        pdfMergerUtility.addSource(file1);
        pdfMergerUtility.addSource(file2);
        pdfMergerUtility.addSource(file3);

        pdfMergerUtility.mergeDocuments(MemoryUsageSetting.setupMainMemoryOnly());
        //System.out.println("pdf created");
        return mergedFile;
//        return renderPdf2(html1,html2,html3,mergedFileName);


//        String pdfName = "DraftTenderNotice";
//        return renderPdf(html, pdfName);

    }


//    public void mergePDFFiles(List<File> files,
//                              String mergedFileName) {
//        try {
//            PDFMergerUtility pdfmerger = new PDFMergerUtility();
//            for (File file : files) {
//                PDDocument document = PDDocument.load(file);
//                pdfmerger.setDestinationFileName(mergedFileName);
//                pdfmerger.addSource(file);
//                pdfmerger.mergeDocuments(MemoryUsageSetting.setupTempFileOnly());
//                document.close();
//
//            }
//        } catch (Exception e) {
//        }
//    }

    public String noToWord(long number) {

        int n = (int) (Math.log10(number) + 1);

        if (number == 0) {
            return null;

        } else {

            String finalval;
            Double val;
            switch (n) {
                case 3:
                    val = Double.valueOf(number / 100);

                    finalval = val + " hundred";
                    break;
                case 4:
                    val = Double.valueOf(number / 1000);

                    finalval = val + "thousand";
                    break;
                case 5:
                    val = (double) number / 1000;

                    finalval = val + " thousand";
                    break;
                case 6:
                    val = (double) number / 100000;

                    finalval = val + " lakh";
                    break;
                case 7:
                    val = (double) number / 100000;
                    finalval = val + " lakh";
                    break;
                case 8:
                    val = (double) number / 10000000;

                    finalval = val + " crore";
                    break;
                case 9:
                    val = (double) number / 10000000;
                    finalval = val + " crore";
                    break;

                default:
                    return null;
            }
            return finalval;

        }
    }

    public static boolean text(String message) {


        try {
            File f = new File(path);
            // append the text file
            FileWriter fileWriter = new FileWriter(f, true);

            // append text to file by using a buffer
            BufferedWriter bw = new BufferedWriter(fileWriter);
            fileWriter.append(message);


            // tell that it has succeed
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public OIIPCRAResponse getFormGDetails(Integer finYrId, String issueNo, Integer agencyId,Integer userId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {

            // FormGDto sumValue = tenderRepositoryImpl.getSum(finYrId, issueNo, agencyId);
            List<AgencyDto> agency = tenderRepositoryImpl.getAgencyData(finYrId, issueNo, agencyId);
            for (int i = 0; i < agency.size(); i++) {
                List<FormGDto> formG = tenderRepositoryImpl.getFormGDetails(finYrId, issueNo, agency.get(i).getId());
                agency.get(i).setFormG(formG);
            }
            if (agency != null) {

                List<OfficeDataDto> officeData = officeDataService.getOfficeData(null, null, userId);
                result.put("agency", agency);
                result.put("officeData", officeData);

                //result.put("formGList", formG);
                //  result.put("sumValue",sumValue);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("All FormG Details");
            } else {
                result.put("agency", agency);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.NOT_FOUND));
                response.setMessage("Record not found.");
            }
        } catch (Exception ex) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    ex.getMessage(),
                    result);
        }
        return response;
    }

    @Override
    public OIIPCRAResponse getAgencyDetailsByFinYr(Integer finYrId) {
        OIIPCRAResponse response = new OIIPCRAResponse();
        Map<String, Object> result = new HashMap<>();
        try {

            List<AgencyDto> agency = tenderRepositoryImpl.getAgencyDetailsByFinYr(finYrId);
            if (agency != null) {
                result.put("agency", agency);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.OK));
                response.setMessage("All AgencyDetails Details");
            } else {
                result.put("agency", agency);
                response.setData(result);
                response.setStatus(1);
                response.setStatusCode(new ResponseEntity<>(HttpStatus.NOT_FOUND));
                response.setMessage("Record not found.");
            }
        } catch (Exception ex) {
            response = new OIIPCRAResponse(0,
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    ex.getMessage(),
                    result);
        }
        return response;
    }

    @Override
    public File getBidderDataSheet(HttpServletResponse exportResponse, Integer bidId, java.sql.Date tenderOpeningDate, Integer agencyId,Integer userId) throws Exception {
        Context context = new Context();
//        context.setVariable("bidId", bidId);
//        context.setVariable("agencyId", agencyId);

//        String html = templateEngine.process("bidder-data-sheet", context);

        String html = " <!DOCTYPE HTML>\n" +
                "<html xmlns:th=\"http://www.thymeleaf.org\">\n" +
                "\n" +
                "<head>\n" +
                "  <meta charset=\"UTF-8\" />\n" +
                "  <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\" />\n" +
                "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />\n" +
                "  <title>Document</title>\n" +
                "  <!--  <link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/2.3.2/css/bootstrap.min.css\"/>-->\n" +
                "  <!--  <link href=\"https://cdn.jsdelivr.net/npm/bootstrap@5.2.1/dist/css/bootstrap.min.css\" rel=\"stylesheet\" integrity=\"sha384-iYQeCzEYFbKjA/T2uDLTpkwGzCiq6soy8tYaI1GyVh/UjpbCx/TYkiZhlZB6+fzT\" crossorigin=\"anonymous\"/>-->\n" +
                "  <!--  <link rel=\"stylesheet\" th:href=\"@{../pdf-resources/css/bootstrap2.css}\"/>-->\n" +
                "\n" +
                "  <style>\n" +
//                ".container {\n" +
//                "\n" +
//                "             width: 760px;\n" +
//                "\n" +
//                "              margin-right: auto;\n" +
//                "    margin-left: auto;\n" +
//                "    *zoom: 1;\n" +
//                "        }\n" +
//                "        .container:before,\n" +
//                ".container:after {\n" +
//                "    display: table;\n" +
//                "    line-height: 0;\n" +
//                "    content: \"\"\n" +
//                "}\n" +
//                "\n" +
//                ".container:after {\n" +
//                "    clear: both\n" +
//                "}\n" +
//                "p{\n" +
//                "font-size:14px;\n" +
//                "}"+
                "        @page {\n" +
                "            size: A4 landscape;\n" +
                "    \n" +
                "        }\n" +
                "        .container {\n" +
                "            padding-top: 30px;\n" +
                "            width: 1000px;\n" +
                "            width: auto;\n" +
                "            margin-right: auto;\n" +
                "            margin-left: auto;\n" +
                "            *zoom: 1;\n" +
                "        }\n" +
                "\n" +
                "        .container:before,\n" +
                "        .container:after {\n" +
                "            display: table;\n" +
                "            line-height: 0;\n" +
                "            content: \"\"\n" +
                "        }\n" +
                "\n" +
                "        .container:after {\n" +
                "            clear: both\n" +
                "        }\n" +
                "\n" +
                "\n" +
                "        .table {\n" +
                "            width: 100%;\n" +
                "            margin-bottom: 20px\n" +
                "        }\n" +
                "\n" +
                "        .table th,\n" +
                "        .table td {\n" +
                "            padding: 8px;\n" +
                "            line-height: 20px;\n" +
                "            text-align: left;\n" +
                "            vertical-align: top;\n" +
                "            border-top: 1px solid #ddd;\n" +
                "            border: 1px solid black;\n" +
                "        }\n" +
                "\n" +
                "        @page {\n" +
                "            @bottom-right {\n" +
                "                content: \"Page \" counter(page) \" of \" counter(pages);\n" +
                "            }\n" +
                "        }\n" +
                "\n" +
                "        @page {\n" +
                "            @bottom-left {\n" +
                "                content: date(\"dd.MM.yyyy HH.mm\");\n" +
                "            }\n" +
                "        }\n" +
                "\n" +
                "        * {\n" +
                "            box-sizing: border-box;\n" +
                "            font-family: arial, sans-serif;\n" +
                "            font-size: x-small;\n" +
                "\n" +
                "        }\n" +
                "        .table {\n" +
                "            width: 100%;\n" +
                "            margin-bottom: 20px\n" +
                "        }\n" +
                "\n" +
                "        .table th,\n" +
                "        .table td {\n" +
                "            padding: 8px;\n" +
                "            line-height: 20px;\n" +
                "            text-align: left;\n" +
                "            vertical-align: top;\n" +
                "            border-top: 1px solid #ddd;\n" +
                "            border: 1px solid black;\n" +
                "        }\n" +
                "\n" +
                "\n" +
                "        .column {\n" +
                "            float: left;\n" +
                "            padding: 10px;\n" +
                "            height: 300px;\n" +
                "            /* Should be removed. Only for demonstration */\n" +
                "        }\n" +
                "\n" +
                "        .left {\n" +
                "            width: 30%;\n" +
                "            margin-right: 35px;\n" +
                "        }\n" +
                "\n" +
                "        .right {\n" +
                "            width: 35%;\n" +
                "        }\n" +
                "\n" +
                "        .middle {\n" +
                "            width: 30%;\n" +
                "        }\n" +
                "        /* Clear floats after the columns */\n" +
                "\n" +
                "        .row:after {\n" +
                "            content: \"\";\n" +
                "            display: table;\n" +
                "            clear: both;\n" +
                "        }\n" +
                //PAGE BREAK
                "div.page1 {\n" +
                "    page-break-after: always;\n" +
                "}" +
                "\n" +
                "        .right {\n" +
                "            float: right;\n" +
                "        }\n" +
                "\n" +
                "        .table1 {\n" +
                "  /* font-family: arial, sans-serif; */\n" +
                "  border-spacing:0;\n" +
                "  border-collapse: collapse;\n" +
                "  width: 100%;\n" +
                "}\n" +
                "\n" +
                "td {\n" +
                "  /* border: 1px solid #dddddd; */\n" +
                "  text-align: left;\n" +
                "  padding: 2px;\n" +
                "}\n" +
                ".box1{\n" +
                "    width: 125px;\n" +
                "    border: 0.5px solid rgb(105, 102, 102);\n" +
                "\n" +
                "}\n" +
                "  span.a {\n" +
                "  display: inline;\n" +
                "  width: 100px;\n" +
                "  height: 100px;\n" +
                "  padding: 1px;\n" +
                "  border: 1px solid black;\n" +
                "}\n" +
                //move box little left
                ".relative {\n" +
                "  position: relative;\n" +
                "  right: 1px;\n" +
                "  width: 250px;\n" +
                "  border: 0.5px solid rgb(105, 102, 102);\n" +
                "}" +
                ".bottom {\n" +
                "        position: absolute;\n" +
                "        bottom: 0;\n" +
                "        width: 100%;\n" +
                "        float: left" +
//                "        padding-left: 1000px ;\n" +
                "      }" +
                //rupee
                ".rupee{\n" +
                "    background-position:center;\n" +
                "    padding-left:15px;\n" +
                "<!--    padding-top:20px;-->\n" +
//                "    width: 2px;\n" +
                "    height: 8px;\n" +
                "    background-image: url('../pdf-resources/images/rupee.png');\n" +
                "    display:block;\n" +
                "    background-repeat: no-repeat;\n" +
                "}\n" +
                ".child{\n" +
                "    display: inline-block;\n" +
                "    padding: 1rem 1rem;\n" +
                "    vertical-align: middle;\n" +
                "}" +
                //heading
//                ".h1 {\n" +
//                "  font-size: 50px;\n" +
//                "}"+
                //footer
                ".bottom {\n" +
                "        position: absolute;\n" +
                "        bottom: 0;\n" +
                "        width: 100%;\n" +
                "        padding-left: 900px;\n" +
                "        padding-bottom: 80px" +
                "      }" +
                //heading
                ".s1 {\n" +
                "            color: #011A2A;\n" +
                "            font-family: Calibri, sans-serif;\n" +
                "            font-style: normal;\n" +
                "            font-weight: normal;\n" +
                "            text-decoration: none;\n" +
                "            font-size: 18pt;\n" +
                "        }" +
                //bottom text
                ".foo {\n" +
                "    position: fixed;\n" +
                "    bottom: 0;\n" +
                "    right: 0;\n" +
                "  }" +
                "    </style>\n" +
                "</head>\n" +
                "\n" +
                "<body> ";


        List<BidderDetailsDto> bidderDetailsDto = tenderRepositoryImpl.getBidderDataSheet(bidId, tenderOpeningDate, agencyId);
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy");
        Date date = new Date();
        for (BidderDetailsDto item : bidderDetailsDto) {
            if (item.getNameOfWork() != null) {
                if (item.getNameOfWork().contains("&")) {
                    item.setNameOfWork(item.getNameOfWork().replace("&", "and"));
                }
            }
            if (item.getAgencyName().contains("&")) {
                item.setAgencyName(item.getAgencyName().replace("&", "and"));
            }
        }
        for (BidderDetailsDto data : bidderDetailsDto) {
                html += "<div class=\"landscapePage\">\n" +
                        "\n" +
                        "  <table width=\"100%\" style=\"border-style: none;\">\n" +
                        "    <tr>\n" +
                        "      <td style=\"text-align:left;border-style: none;\"><img src=\"../pdf-resources/images/logo.png\" /></td>\n" +
                        "      <td style=\"text-align:left;border-style: none;\">\n" +
                        "        <h1><span class=\"box\"><strong><span class=\"s1\">Tender Opening Data: OIIPCRA   &nbsp; &nbsp; &nbsp;(Technical Qualification)</span>  &nbsp;  &nbsp;&nbsp;  &nbsp;  &nbsp;   &nbsp; <span style=\"font-size: 15px;\"> " + formatter.format(date) + "</span></strong></span></h1> <br/>\n" +
                        "      </td>\n" +
                        "\n" +
                        "    </tr>\n" +
                        "  </table>\n";
                html += "<hr/>" +
                        "<strong>  &nbsp;    &nbsp;    &nbsp; &nbsp;     Agency: </strong> <span style=\"color: darkred;\">&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; " + data.getAgencyName() + " " + data.getLicenseClass() + " Contractor </span>   &nbsp; &nbsp; &nbsp;   &nbsp; &nbsp;      &nbsp; &nbsp; <strong></strong> Name of Work:  &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; <span style=\"color: darkred;\"> &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; " + data.getNameOfWork() + " in " + data.getDistName() + " District</span>\n" +
                        "  <hr/>\n" +
                        "  <br/>";

                html += "<div class=\"page1\">\n" +
                        "    <div class=\"column left\">\n" +
                        "\n" +
                        "      <table class=\"table1\" cellspacing=\"0\" >\n" +
                        "        <tr>\n" +
                        "          <td>ID:</td>\n" +
                        "          <td style=\"text-align: right;\" colspan=\"2\">  <div class=\"box1\" style=\"float: right;\">" + data.getId() + "</div> </td>\n" +
                        "        </tr>";
                html += "<tr>\n" +
                        "          <td>Bid ID :</td>\n" +
                        "          <td style=\"text-align: right;\" colspan=\"2\"><div class=\"box1\" style=\"float: right;\">" + data.getBidId() + "</div></td>\n" +
                        "        </tr>\n" +
                        "        <tr>\n" +
                        "          <td>Bidding Type</td>\n" +
                        "          <td style=\"text-align: right;\" colspan=\"2\"><div class=\"box1\" style=\"float: right;\">" + data.getBiddingType() + "</div></td>\n" +
                        "        </tr>\n" +
                        "        <tr>\n" +
                        "          <td>Date of opening of tender</td>\n" +
                        "          <td style=\"text-align: right;\" colspan=\"2\"><div class=\"box1\" style=\"float: right;\">" + data.getTenderOpeningdateChar() + "</div></td>\n" +
                        "        </tr>\n" +
                        "        <tr>\n" +
                        "          <td>Name Of Work</td>\n" +
                        "          <td style=\"text-align: right;\" colspan=\"2\"><div class=\"box1\" style=\"float: right;\">" + data.getNameOfWork() + "</div> </td>\n" +
                        "        </tr>\n" +
                        "        <tr>\n" +
                        "          <td>Work ID:</td>\n" +
                        "          <td style=\"text-align: right;\" colspan=\"2\"><div class=\"box1\" style=\"float: right;\">" + data.getWorkId() + "</div></td>\n" +
                        "        </tr>\n" +
                        "        <tr>\n" +
                        "          <td>Scheme of funding:</td>\n" +
                        "          <td style=\"text-align: right;\" colspan=\"2\"><div class=\"box1\" style=\"float: right;\">" + data.getProjectName() + "</div></td>\n" +
                        "        </tr>\n" +
                        "        <tr>\n" +
                        "          <td>Work Sl No in TCN:</td>\n" +
                        "          <td style=\"text-align: right;\" colspan=\"2\"><div class=\"box1\" style=\"float: right;\">" + data.getWorkSlNoInTcn() + "</div></td>\n" +
                        "        </tr>\n" +
                        "        <tr>\n" +
                        "          <td>Amount put to tender:</td>\n" +
                        "          <td style=\"text-align: right;\" colspan=\"2\"><div class=\"box1\" style=\"float: right;\"><img src=\"../pdf-resources/images/rupee.png\" width=\"6px\"/>" + data.getTenderAmountChar() + "</div></td>\n" +
                        "        </tr>\n" +
                        "        <tr>\n" +
                        "          <td>Paper Cost Specified:</td>\n" +
                        "          <td style=\"text-align: right;\" colspan=\"2\"><div class=\"box1\" style=\"float: right;\"><img src=\"../pdf-resources/images/rupee.png\" width=\"6px\"/>" + data.getPaperCost() + "</div></td>\n" +
                        "        </tr>\n" +
                        "        <tr>\n" +
                        "          <td>EMD to be Deposited:</td>\n" +
                        "          <td style=\"text-align: right;\" colspan=\"2\"><div class=\"box1\" style=\"float: right;\"><img src=\"../pdf-resources/images/rupee.png\" width=\"6px\"/>" + data.getEmdToDeposit() + "</div></td>\n" +
                        "        </tr>";
            String blankPrint="&nbsp;";
                html += "<tr>\n" +
                        "          <td>Period of Completion:</td>\n" +
                        "          <td style=\"text-align: right;\" colspan=\"2\"><span><div class=\"box1\" style=\"float: right;\" >" + data.getTimeOfCompletion() + "  Days</div></span></td>\n" +
                        "        </tr>\n" +
                        "        <tr>\n" +
                        "          <td>Name of District:</td>\n" +
                        "          <td style=\"text-align: right;\" colspan=\"2\"><div class=\"box1\" style=\"float: right;\">" + data.getDistName() + "</div></td>\n" +
                        "        </tr>\n" +
                        "        <tr>\n" +
                        "          <td>Name of MI Division:</td>\n" ;
                if(data.getDivName() != null){
                    html += "          <td style=\"text-align: right;\" colspan=\"2\"><div class=\"box1\" style=\"float: right;\">" + data.getDivName() + "</div></td>\n" ;
                } else {
                    html += "          <td style=\"text-align: right;\" colspan=\"2\"><div class=\"box1\" style=\"float: right;\">" + blankPrint + "</div></td>\n" ;

                }
                        html += "        </tr> <tr>\n" +
                        "          <td>Name of Block:</td>\n" ;
                if(data.getBlockName() != null){
                    html += "          <td style=\"text-align: right;\" colspan=\"2\"><div class=\"box1\" style=\"float: right;\">" + data.getBlockName() + "</div></td>\n" ;
                } else {
                    html += "          <td style=\"text-align: right;\" colspan=\"2\"><div class=\"box1\" style=\"float: right;\"> "+blankPrint+"</div></td>\n" ;
                }

                html += " </tr></table>" +
                        "</div>";
                html += "<div class=\"column middle\" >\n" +
                        "      <table class=\"table2\">\n" +
                        "        <h2> 1. BIDDER DETAILS VALIDITY </h2>\n" +
                        "        <tr>\n" +
                        "          <td>Agency Name:</td>\n" +
                        "          <td style=\"text-align: right;\" colspan=\"1\"><div class=\"box1\" style=\"float: right;\">" + data.getAgencyName() + "</div></td>\n" +
                        "        </tr>\n" +
                        "        <tr>\n" +
                        "          <td>Class of License:</td>\n" +
                        "          <td style=\"text-align: right;\" colspan=\"1\"><span><div class=\"box1\" style=\"float: right;\"></div><div class=\"box1\" style=\"float: right;\">" + data.getLicenseClass() + " " + data.getLicenseName() + "</div></span></td>\n" +
                        "        </tr>\n" +
                        "        <tr>\n" +
                        "          <td>License Validity:</td>\n" +
                        "          <td style=\"text-align: right;\" colspan=\"1\"><span><div class=\"box1\" style=\"float: right;\"></div>    <div class=\"box1\" style=\"float: right;\">" + data.getLicenseExpiring() + " License OK</div></span></td>\n" +
                        "        </tr>" +
                        "        <tr>\n" +
                        "          <td>Bidder Category:</td>\n" +
                        "          <td style=\"text-align: right;\" colspan=\"1\"><div class=\"box1\" style=\"float: right;\">" + data.getBidderCategory() + "</div></td>\n" +
                        "        </tr>\n" +
                        "        <tr>\n" +
                        "          <td>GST IN:</td>\n" +
                        "          <td style=\"text-align: right;\" colspan=\"1\"><div class=\"box1\" style=\"float: right;\">" + data.getGstIn() + "</div></td>\n" +
                        "        </tr>\n" +
                        "        <tr>\n" +
                        "          <td>Mobile No:</td>\n" ;
                if(data.getContactNo() != null){
                    html += "          <td style=\"text-align: right;\" colspan=\"1\"><div class=\"box1\" style=\"float: right;\">" + data.getContactNo() + "</div></td>\n" ;
                } else {
                    html += "          <td style=\"text-align: right;\" colspan=\"1\"><div class=\"box1\" style=\"float: right;\">" + blankPrint + "</div></td>\n" ;
                }
                        html += "        </tr>\n" +
                        "        <tr>\n" +
                        "          <td>Affidavit Validity:</td>\n" +
                        "          <td style=\"text-align: right;\" colspan=\"1\"><div class=\"box1\" style=\"float: right;\">" + data.getAffidavitValid() + "</div></td>\n" +
                        "        </tr>\n" +
                        "        <tr>\n" +
                        "          <td>Paper Cost Submitted:</td>\n" +
                        "          <td style=\"text-align: right;\" colspan=\"1\"><span><div class=\"box1\" style=\"float: right;\"></div>      <div class=\"box1\" style=\"float: right;\"><img src=\"../pdf-resources/images/rupee.png\" width=\"6px\"/> " + data.getPaperCostSubmit() + " /-</div></span></td>\n" +
                        "        </tr>\n" +
                        "        <tr>\n" +
                        "          <td>Paper Cost Validity:</td>\n" +
                        "          <td style=\"text-align: right;\" colspan=\"1\"><div class=\"box1\" style=\"float: right;\">" + data.getPaperCostValidity() + "</div></td>\n" +
                        "        </tr>\n" +
                        "        <tr>\n" +
                        "          <td>EMD Deposited:</td>\n" +
                        "          <td style=\"text-align: right;\" colspan=\"1\"><span><div class=\"box1\" style=\"float: right;\"></div>    <div class=\"box1\" style=\"float: right;\"><img src=\"../pdf-resources/images/rupee.png\" width=\"6px\"/> " + data.getEmdToDeposit() + " " + data.getEmdDepositType() + "</div></span></td>\n" +
                        "        </tr>\n" +
                        "        <tr>\n" +
                        "          <td>EMD Validity:</td>\n" +
                        "          <td style=\"text-align: right;\" colspan=\"1\"><div class=\"box1\" style=\"float: right;\">" + data.getEmdValidity() + "</div></td>\n" +
                        "        </tr>\n" +
                        "      </table>\n" +
                        "\n";
            List<CompletionOfSimilarTypeOfWorkDto> completionOfSimilarWorkType = tenderRepositoryImpl.getCompletionOfSimilarWorkTypeByBidderId(data.getId());

                html += "      <table class=\"table3\">\n";
            if(completionOfSimilarWorkType.size() > 0){
                html += "   <h2> 2. COMPLETION OF SIMILAR TYPE OF WORK VALUED (Lakh Rs. <img src=\"../pdf-resources/images/rupee.png\" width=\"8px\"/><span style=\"color: red;\"> " + completionOfSimilarWorkType.get(3).getValue() + " </span>) </h2>\n" ;
            } else {
                html += "   <h2> 2. COMPLETION OF SIMILAR TYPE OF WORK VALUED (Lakh Rs. <img src=\"../pdf-resources/images/rupee.png\" width=\"8px\"/><span style=\"color: red;\"> 0.00 </span>) </h2>\n" ;
            }
                        html += "        <tr>\n" +
                        "          <td>Similar Work Value for:</td>\n" +
                        "          <td style=\"text-align: left;\" colspan=\"5\">\n";

//                String valFin4 = "";
//                String valFin3 = "";
//                String valFin2 = "";
//                String valFin1 = "";
//                DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
//                String currentDateTime = dateFormat.format(tenderOpeningDate);
//                Date datee = dateFormat.parse(currentDateTime);
//                Calendar cal = Calendar.getInstance();
//                cal.setTime(datee);
//                int currentYear = cal.get(Calendar.YEAR);
//                int currentMonth = cal.get(Calendar.MONTH);
//                int month = currentMonth + 1;

//                if (month <= 3) {
//                    valFin4 += currentYear - 1 + "-" + currentYear % 100;
//                    valFin3 += currentYear - 2 + "-" + (currentYear - 1) % 100;
//                    valFin2 += currentYear - 3 + "-" + (currentYear - 2) % 100;
//                    valFin1 += currentYear - 4 + "-" + (currentYear - 3) % 100;
//                } else {
//                    valFin4 = currentYear + "-" + (currentYear + 1);
//                    valFin3 = currentYear - 1 + "-" + currentYear;
//                    valFin2 = currentYear - 2 + "-" + (currentYear - 1);
//                    valFin1 = currentYear - 3 + "-" + (currentYear - 2);
//                }

//                Integer a = completionOfSimilarWorkType.size();
//                Integer noOfColumn = 0;
//                if (a < 4) {
//                    if (a == 0) {
//                        noOfColumn = 4;
//                    }
//                    if (a == 1) {
//                        noOfColumn = 3;
//                    }
//                    if (a == 2) {
//                        noOfColumn = 2;
//                    }
//                    if (a == 3) {
//                        noOfColumn = 1;
//                    }

            if(completionOfSimilarWorkType.size() > 0){
                html += "<input type=\"text\" value=\"" + completionOfSimilarWorkType.get(0).getFinYrName() + "\"    class=\"box1\"   style=\"width: 18%;\"/>\n";
                html += "<input type=\"text\" value=\"" + completionOfSimilarWorkType.get(1).getFinYrName() + "\"    class=\"box1\"   style=\"width: 18%;\"/>\n";
                html += "<input type=\"text\" value=\"" + completionOfSimilarWorkType.get(2).getFinYrName() + "\"    class=\"box1\"   style=\"width: 18%;\"/>\n";
                html += "<input type=\"text\" value=\"" + completionOfSimilarWorkType.get(3).getFinYrName() + "\"    class=\"box1\"   style=\"width: 18%;\"/>\n";
            } else {
                html += "<input type=\"text\" value=\" \"    class=\"box1\"   style=\"width: 18%;\"/>\n";
                html += "<input type=\"text\" value=\" \"    class=\"box1\"   style=\"width: 18%;\"/>\n";
                html += "<input type=\"text\" value=\" \"    class=\"box1\"   style=\"width: 18%;\"/>\n";
                html += "<input type=\"text\" value=\" \"    class=\"box1\"   style=\"width: 18%;\"/>\n";
            }


//                }

                html += "          </td>\n" +
                        "        </tr>\n";

                html += "        <tr>\n" +
                        "          <td>Min Agmt Value(Lakh Rs.):</td>\n" +
                        "          <td style=\"text-align: left;\" colspan=\"5\">\n";

//                Double val4 = data.getTenderAmount() * 80 / 100;
//                Double val3 = val4 / 1.05;
//                Double val2 = val3 / 1.05;
//                Double val1 = val2 / 1.05;
                if(completionOfSimilarWorkType.size() > 0){
                    html += " <input type=\"text\" value=\"" + completionOfSimilarWorkType.get(0).getValue() + "\"  class=\"box1\"   style=\"width: 25%;\"/>\n";
                    html += " <input type=\"text\" value=\"" + completionOfSimilarWorkType.get(1).getValue() + "\"  class=\"box1\"   style=\"width: 25%;\"/>\n";
                    html += " <input type=\"text\" value=\"" + completionOfSimilarWorkType.get(2).getValue() + "\"  class=\"box1\"   style=\"width: 25%;\"/>\n";
                    html += " <input type=\"text\" value=\"" + completionOfSimilarWorkType.get(3).getValue() + "\"  class=\"box1\"   style=\"width: 25%;\"/>\n";
                } else {
                    html += " <input type=\"text\" value=\" \"  class=\"box1\"   style=\"width: 25%;\"/>\n";
                    html += " <input type=\"text\" value=\" \"  class=\"box1\"   style=\"width: 25%;\"/>\n";
                    html += " <input type=\"text\" value=\" \"  class=\"box1\"   style=\"width: 25%;\"/>\n";
                    html += " <input type=\"text\" value=\" \"  class=\"box1\"   style=\"width: 25%;\"/>\n";
                }


                html += "          </td>\n" +
                        "        </tr>\n";
                html += "        <tr>\n" +
                        "          <td>Similar Work Executed Year/Agmt Amount:</td>\n" +
                        "          <td >\n" +
                        "\n";

//            List<CompletionOfSimilarTypeOfWorkDto> getYearAndValue = tenderRepositoryImpl.getCompletionOfSimilarWorkTypeByBidderId(data.getId());

            if(completionOfSimilarWorkType.size() > 0 ){
                html += "            <input type=\"text\" value=\"" + completionOfSimilarWorkType.get(0).getExecutedYearName() + "\"  class=\"box1\"    style=\"width: 18%;\"/>\n" +
                        "            <input type=\"text\" value=\"" + completionOfSimilarWorkType.get(0).getSimilarWorkAmount() + "\"   class=\"box1\"   style=\"width: 18%;\"/>\n" +
                        "          </td>\n" +
                        "        </tr>";


                html += "      <tr>    <td>Amount of Work Executed out of the above:</td>\n" +
                        "          <td colspan=\"1\">\n" +
                        "            <input type=\"text\" value=\"" + completionOfSimilarWorkType.get(0).getCompletedAmount() + "\"  class=\"box1\"   style=\"width: 18%;\"/>\n" + //completedamt
                        "            <input type=\"text\" value=\"" + completionOfSimilarWorkType.get(0).getPercentageCompleted() + "%\"   class=\"box1\"   style=\"width: 18%;\"/>\n" + //percentcompleted
                        "          </td>\n" +
                        "        </tr>\n";
            } else {
                html += "            <input type=\"text\" value=\"  \"  class=\"box1\"    style=\"width: 18%;\"/>\n" +
                        "            <input type=\"text\" value=\"  \"   class=\"box1\"   style=\"width: 18%;\"/>\n" +
                        "          </td>\n" +
                        "        </tr>"+
                        "      <tr>    " +
                        "        <td>Amount of Work Executed out of the above:</td>\n" +
                        "          <td colspan=\"1\">\n" +
                        "            <input type=\"text\" value=\"  \"  class=\"box1\"   style=\"width: 18%;\"/>\n" + //completedamt
                        "            <input type=\"text\" value=\"  \"   class=\"box1\"   style=\"width: 18%;\"/>\n" + //percentcompleted
                        "          </td>\n" +
                        "        </tr>\n";
            }
//            }//to here
            List<BidderDetailsDto> bidderDetails = tenderRepositoryImpl.getBidderDetailsByIdforPdf(data.getId());
            String similarWorkExperience = "";
            String turnOverQual = "";
            String isBidQualified = "";
            for (BidderDetailsDto bidderById : bidderDetails) {


                if (bidderById.getCompletionWorkValueQualified() != null && bidderById.getCompletionWorkValueQualified() == true) {
                    similarWorkExperience = "Qualified";
                } else {
                    similarWorkExperience = "Not Qualified";
                }
                if (bidderById.getTurnOverQualified() == true) {
                    turnOverQual = "Qualified";
                } else {
                    turnOverQual = "Not Qualified";
                }
                if (bidderById.getIsBidQualified() == true) {
                    isBidQualified = "Eligible";
                } else {
                    isBidQualified = "Not Eligible";
                }
                html += "        <tr>\n" +
                        "          <td>Similar Work Experience:</td>\n" +
                        "          <td style=\"text-align: left;\" colspan=\"1\">\n" +
                        "            <input type=\"text\" value=\"" + similarWorkExperience + "\" class=\"box1\" style=\" color: rgb(240, 13, 13); font-weight: bolder;\"/>\n" +
                        "          </td>\n" +//completion work value
                        "        </tr>\n" +
                        "      </table>" +
                        "</div>";
                html += "<div class=\"column right\">\n" +
                        "      <table class=\"table4\">\n" ;
                List<AnnualFinancialTurnoverMasterDto> annualFinTurnover = tenderRepositoryImpl.getAnnualFinancialTurnOver(data.getId());
//                for (AnnualFinancialTurnoverMasterDto valYearAndQualified : annualFinTurnover) {
                Double valueForTornOverAvail1 = tenderRepositoryImpl.getValueForTornOverAvail(data.getId());
                if(annualFinTurnover.size() > 0){
                    html += " <h2> 3. ANNUAL FINANCIAL TURNOVER in Lakh Rs. <span style=\"color: red;\"><img src=\"../pdf-resources/images/rupee.png\" width=\"8px\"/> " + annualFinTurnover.get(3).getTurnOverRequired() + " </span> </h2>\n";
                } else {
                    html += " <h2> 3. ANNUAL FINANCIAL TURNOVER in Lakh Rs. <span style=\"color: red;\"><img src=\"../pdf-resources/images/rupee.png\" width=\"8px\"/> 0.00 </span> </h2>\n";
                }
                    html += "<tr>\n" +
                            "<td>Turnover Years:</td>\n" +
                            "<td style=\"text-align: left;\" colspan=\"5\">\n";


//                    Integer size = annualFinTurnover.size();
//                    Integer numOfColumn = 0;
//                    if (size < 4) {
//                        if (size == 0) {
//                            numOfColumn = 4;
//                        }
//                        if (size == 1) {
//                            numOfColumn = 3;
//                        }
//                        if (size == 2) {
//                        }
//                        if (size == 3) {
//                            numOfColumn = 1;
//                        }
                        if(annualFinTurnover.size() > 0){
                            html += " <input type=\"text\" value=\"" + annualFinTurnover.get(0).getFinancialYearName() + "\"  class=\"box1\" style=\"width: 18%;\"/> \n";
                            html += " <input type=\"text\" value=\"" + annualFinTurnover.get(1).getFinancialYearName() + "\"  class=\"box1\"    style=\"width: 18%;\"/>\n";
                            html += " <input type=\"text\" value=\"" + annualFinTurnover.get(2).getFinancialYearName() + "\"  class=\"box1\"    style=\"width: 18%;\"/>\n";
                            html += " <input type=\"text\" value=\"" + annualFinTurnover.get(3).getFinancialYearName() + "\"  class=\"box1\"    style=\"width: 28%;\"/>\n";
                        } else {
                            html += " <input type=\"text\" value=\"  \"  class=\"box1\" style=\"width: 18%;\"/> \n";
                            html += " <input type=\"text\" value=\"  \"  class=\"box1\"    style=\"width: 18%;\"/>\n";
                            html += " <input type=\"text\" value=\"  \"  class=\"box1\"    style=\"width: 18%;\"/>\n";
                            html += " <input type=\"text\" value=\"  \"  class=\"box1\"    style=\"width: 28%;\"/>\n";
                        }
//                    }
                    html += "</td>\n" +
                            "        </tr>";
//                    Double valAnnualTurn4 = data.getTenderAmount() * 1.05;
//                    Double valAnnualTurn3 = valAnnualTurn4 / 1.05;
//                    Double valAnnualTurn2 = valAnnualTurn3 / 1.05;
//                    Double valAnnualTurn1 = valAnnualTurn2 / 1.05;

                    html += "<tr>\n" +
                            "          <td>Turnover Required:</td>\n" +
                            "          <td style=\"text-align: left;\" colspan=\"1\">\n" ;
                if(annualFinTurnover.size() > 0) {
                    html += "            <input type=\"text\" value=\"" + annualFinTurnover.get(0).getTurnOverRequired() + "\"   class=\"box1\"   style=\"width: 18%;\"/>\n" +
                            "            <input type=\"text\" value=\"" + annualFinTurnover.get(1).getTurnOverRequired() + "\"   class=\"box1\"   style=\"width: 18%;\"/>\n" +
                            "            <input type=\"text\" value=\"" + annualFinTurnover.get(2).getTurnOverRequired() + "\"   class=\"box1\"   style=\"width: 18%;\"/>\n" +
                            "            <input type=\"text\" value=\"" + annualFinTurnover.get(3).getTurnOverRequired() + "\"   class=\"box1\"   style=\"width: 35%;\"/>\n" ;
                } else {
                    html += "            <input type=\"text\" value=\" 0.00 \"   class=\"box1\"   style=\"width: 18%;\"/>\n" +
                            "            <input type=\"text\" value=\" 0.00 \"   class=\"box1\"   style=\"width: 18%;\"/>\n" +
                            "            <input type=\"text\" value=\" 0.00 \"   class=\"box1\"   style=\"width: 18%;\"/>\n" +
                            "            <input type=\"text\" value=\" 0.00 \"   class=\"box1\"   style=\"width: 35%;\"/>\n" ;
                }
                            html += "          </td>\n" +
                            "        </tr>" +
                            "<tr>\n";
//                    Double valueForTornOverAvail1 = tenderRepositoryImpl.getValueForTornOverAvail(data.getId());
//                    Double equivalentValue1=valueForTornOverAvail1*((1.05*1.05*1.05));
                    html += "          <td>Turnover Available:</td>\n" +

                            "          <td style=\"text-align: left;\" colspan=\"1\">\n";

                if(annualFinTurnover.size() > 0) {
                    html += "            <input type=\"text\" value=\"" + annualFinTurnover.get(0).getValue() + "\"  class=\"box1\"    style=\"width: 18%; \"/>\n"+
                            "    <input type=\"text\" value=\" " + annualFinTurnover.get(1).getValue() + " \" class=\"box1\"     style=\"width: 18%; \"/>\n" +
                            "            <input type=\"text\" value=\" " + annualFinTurnover.get(2).getValue() + " \" class=\"box1\"     style=\"width: 18%; \"/>\n" +
                            "            <input type=\"text\" value=\" " + annualFinTurnover.get(3).getValue() + " \"  class=\"box1\"   style=\"width: 28%; \"/>\n";
                } else {
                    html += "            <input type=\"text\" value=\" 0.00 \"  class=\"box1\"    style=\"width: 18%; \"/>\n" +
                            "            <input type=\"text\" value=\" 0.00  \" class=\"box1\"     style=\"width: 18%; \"/>\n" +
                            "            <input type=\"text\" value=\" 0.00  \" class=\"box1\"     style=\"width: 18%; \"/>\n" +
                            "            <input type=\"text\" value=\" 0.00 \"  class=\"box1\"   style=\"width: 28%; \"/>\n";
                }
                    html += "          </td>\n" +
                            "        </tr>" +
                            "<tr>\n" +
                            "          <td>Eqivalent:</td>\n" +
                            "          <td style=\"text-align: left;\" colspan=\"1\">\n";
                if(annualFinTurnover.size() > 0) {
                        html += "            <input type=\"text\" value=\""+annualFinTurnover.get(0).getEquivalent() + "\" class=\"box1\"     style=\"width: 18%; \"/>\n"+
                            "            <input type=\"text\" value=\" "+annualFinTurnover.get(1).getEquivalent()+" \" class=\"box1\"     style=\"width: 18%;\"/>\n" +
                            "            <input type=\"text\" value=\" "+annualFinTurnover.get(2).getEquivalent()+"\"  class=\"box1\"    style=\"width: 18%; color: red; \"/>\n" +
                            "            <input type=\"text\" value=\" "+annualFinTurnover.get(3).getEquivalent()+"\"    class=\"box1\"  style=\"width: 28%; \"/>\n" ;
                } else {
                    html += "            <input type=\"text\" value=\" 0.00 \" class=\"box1\"     style=\"width: 18%; \"/>\n"+
                            "            <input type=\"text\" value=\" 0.00  \" class=\"box1\"     style=\"width: 18%;\"/>\n" +
                            "            <input type=\"text\" value=\" 0.00 \"  class=\"box1\"    style=\"width: 18%; color: red; \"/>\n" +
                            "            <input type=\"text\" value=\" 0.00 \"    class=\"box1\"  style=\"width: 28%; \"/>\n" ;
                }
                    html += "          </td>\n" +
                            "        </tr>\n";

                    String year="";
                    Double maxTurn=0.00;
                    for(int i=0; i<annualFinTurnover.size(); i++){
                        if(annualFinTurnover.get(i).getMaximum() == true){
                            year=annualFinTurnover.get(i).getFinancialYearName();
                            maxTurn=annualFinTurnover.get(i).getEquivalent();
                        }
                    }

//<img src="../pdf-resources/images/rupee.png" width="8px"/>
                    html += "        <tr>\n" +
                            "          <td>Turnover Maximum:</td>\n" +
                            "          <td style=\"text-align: left;\" colspan=\"1\">\n" +
                            "            <input type=\"text\" value=\"" + year + "\"  class=\"box1\"    style=\"width: 18%; font-weight: bolder;\"/>\n" + //value
                            "            <input type=\"text\" value=\"" + maxTurn + "\" class=\"box1\"     style=\"width: 18%; font-weight: bolder;\"/>\n" + //finYear
                            "\n" +
                            "          </td>\n" +
                            "        </tr>\n" +
                            "        <tr>\n" +
                            "          <td>Financial Turnover:</td>\n" +
                            "          <td style=\"text-align: left;\" colspan=\"1\">\n" +
                            "            <input type=\"text\" value=\"" + turnOverQual + "\" class=\"box1\"  style=\" font-weight: bolder;\"/>\n" + //TurnOverQualified(BidderDetails)
                            "          </td>\n" +
                            "        </tr>\n";

//                }
                html += "</table><div>";

                List<LiquidAssetAvailability> liquidAssetAvailability = tenderRepositoryImpl.getLiquidAssetAvailabilityByBidderId(data.getId());
                if(liquidAssetAvailability.size() > 0){
                    for (LiquidAssetAvailability liquidAssetData : liquidAssetAvailability) {
                    Double liquidTopData = null;
                    double complData = Double.parseDouble(String.valueOf(data.getTimeOfCompletion()))/30;

                    liquidTopData = (data.getTenderAmount()/complData*3)/100000;


                        if (liquidAssetData.getBankName() != null) {
                            if (liquidAssetData.getBankName().contains("&")) {
                                liquidAssetData.setBankName((liquidAssetData.getBankName().replace("&", "and")));
                            }
                        }
                        //another table
                        html += "<table class=\"table5\">\n" +
                                "        <h2> 4. AVAILABILITY OF LIQUID ASSETS (3 Months) in Lakh Rs. <img src=\"../pdf-resources/images/rupee.png\" width=\"8px\"/><span style=\"color: red;\"> "+String.format("%.2f", liquidTopData)+" </span> </h2>\n" +
                                "        <tr>\n" +
                                "          <td>Name of the Bank:</td>\n" +
                                "          <td style=\"text-align: right;\" colspan=\"2\">\n" +
                                "            <input type=\"text\"  value=\"" + liquidAssetData.getBankName() + "\" class=\"relative\" />\n" +
                                "          </td>\n" +
                                "        </tr>\n" +
                                "        <tr>\n" +
                                "          <td>Amount of Credit Rs.:</td>\n" +
                                "          <td style=\"text-align: left;\" colspan=\"1\">\n" +
                                "            <input type=\"text\" value=\"" + liquidAssetData.getCreditAmount() + "\" class=\"box1\"  />\n" +
                                "          </td>\n" +
                                "        </tr>\n" +
                                "        <tr>\n" +
                                "          <td>Amount of Liquidity Rs.:</td>\n" +
                                "          <td style=\"text-align: left;\" colspan=\"1\">\n" +
                                "            <input type=\"text\" value=\"" + liquidAssetData.getLiquidityAmount() + "\" class=\"box1\"/>\n" +
                                "          </td>\n" +
                                "        </tr>\n" +
                                "        <tr>\n" +
                                "          <td>Total Liquid Assets Rs.:</td>\n" +
                                "          <td style=\"text-align: left;\" colspan=\"1\">\n" +
                                "            <input type=\"text\" value=\"" + liquidAssetData.getTotalLiquidAsset() + "\" class=\"box1\" style=\"font-weight: bolder;\"/>\n" +
                                "          </td>\n" +
                                "        </tr>\n" +
                                "        <tr>\n" +
                                "          <td>Availability of Credit:</td>\n" +
                                "          <td style=\"text-align: left;\" colspan=\"1\">\n" +
                                "            <input type=\"text\" value=\"" + bidderById.getLiquidAssetQualifiedString() + "\" class=\"box1\" style=\" font-weight: bolder;\"/>\n" +
                                "          </td>\n" +
                                "        </tr>\n" +
                                "        <tr>\n" +
                                "          <td>Bid Eligibility:</td>\n" +
                                "          <td style=\"text-align: left;\" colspan=\"1\">\n" +
                                "            <input type=\"text\" value=\"" + isBidQualified + "\" class=\"box1\" style=\" color: red; font-weight: bolder; \"/>\n" +
                                "\n" +
                                "          </td>\n" +
                                "        </tr>\n" +
                                "      </table>";
//                            "</div></div>";
                    }
                } else {
                    html += "<table class=\"table5\">\n" +
                            "        <h2> 4. AVAILABILITY OF LIQUID ASSETS (3 Months) in Lakh Rs. <img src=\"../pdf-resources/images/rupee.png\" width=\"8px\"/><span style=\"color: blue;\"> null </span> </h2>\n" +
                            "        <tr>\n" +
                            "          <td>Name of the Bank:</td>\n" +
                            "          <td style=\"text-align: right;\" colspan=\"2\">\n" +
                            "            <input type=\"text\"  value=\" \" class=\"relative\" />\n" +
                            "          </td>\n" +
                            "        </tr>\n" +
                            "        <tr>\n" +
                            "          <td>Amount of Credit Rs.:</td>\n" +
                            "          <td style=\"text-align: left;\" colspan=\"1\">\n" +
                            "            <input type=\"text\" value=\" \" class=\"box1\"  />\n" +
                            "          </td>\n" +
                            "        </tr>\n" +
                            "        <tr>\n" +
                            "          <td>Amount of Liquidity Rs.:</td>\n" +
                            "          <td style=\"text-align: left;\" colspan=\"1\">\n" +
                            "            <input type=\"text\" value=\" \" class=\"box1\"/>\n" +
                            "          </td>\n" +
                            "        </tr>\n" +
                            "        <tr>\n" +
                            "          <td>Total Liquid Assets Rs.:</td>\n" +
                            "          <td style=\"text-align: left;\" colspan=\"1\">\n" +
                            "            <input type=\"text\" value=\" \" class=\"box1\" style=\"font-weight: bolder;\"/>\n" +
                            "          </td>\n" +
                            "        </tr>\n" +
                            "        <tr>\n" +
                            "          <td>Availability of Credit:</td>\n" +
                            "          <td style=\"text-align: left;\" colspan=\"1\">\n" +
                            "            <input type=\"text\" value=\" \" class=\"box1\" style=\" font-weight: bolder;\"/>\n" +
                            "          </td>\n" +
                            "        </tr>\n" +
                            "        <tr>\n" +
                            "          <td>Bid Eligibility:</td>\n" +
                            "          <td style=\"text-align: left;\" colspan=\"1\">\n" +
                            "            <input type=\"text\" value=\" \" class=\"box1\" style=\" color: red; font-weight: bolder; \"/>\n" +
                            "\n" +
                            "          </td>\n" +
                            "        </tr>\n" +
                            "      </table>";
                }

                html += "</div></div>";
                    UserInfoDto user = userQueryRepository.getUserById(userId);
                    Integer distId = null;
                    if (user.getUserLevelId() == 1) {
                        distId = 0;
                    }
                    List<Integer> disIds = null;
                    if (user.getUserLevelId() == 2) {
                        disIds = officeDataService.getDistId(userId);
                    }
                    Integer divisionId = null;
                    if (user.getUserLevelId() == 6) {
                        divisionId = officeDataRepositoryImpl.getDivisionIdByUserId(userId);
                    }

                    List<OfficeDataDto> officeData = officeDataService.getOfficeDataDetails(distId, divisionId, userId, disIds);
                    for (OfficeDataDto ofData : officeData) {
                        if (user.getRoleId() <= 4) {
                            html += "<br/>\n" +
                                    "<p style=\"float: right; margin-top: 250px; margin-right: 30px;\" class=\"bottom;\"><hr style=\"width: 180px; border: none; border-bottom: 1px solid black;\"/><br/><span>" + ofData.getDesignationName() + "</span></p>";
                        } else {
                            html += "<br/>\n" +
                                    "<p style=\"float: right; margin-top: 250px; margin-right: 30px;\" class=\"bottom;\"><hr style=\"width: 180px; border: none; border-bottom: 1px solid black;\"/><br/><span>" + ofData.getDesignationName() + ", (" + ofData.getDivisionName() + ")</span></p>";
                        }
                        html += //"<p style=\"float: right; margin-top: 250px; margin-right: 40px;\" class=\"bottom;\"><hr style=\"width: 180px; border: none; border-bottom: 1px solid black;\"/><br/><span>Dy. Executive Engineer, &nbsp; OIIPCRA</span></p>" +
                                " <p style=\"float: right; margin-top: 250px; margin-right: 40px;\" class=\"bottom;\"><hr style=\"width: 180px; border: none; border-bottom: 1px solid black;\"/><br/><span>Asst. Executive Engineer  &nbsp; </span></p> ";
                    }
//                }
            }
            html += "</div>" +
                    "</div>";
        }
        html += "</body>\n" +
                "</html>";

//        String html = templateEngine.process("bidder-data-sheet", context);

        String pdfName = "BidderDataSheet";

        return renderPdf(html, pdfName);
    }
    @Override
    public List<BidderDetailsDto> getBidderByBidIdDD(Integer bidId) {
        return tenderRepositoryImpl.getBidderByBidIdDD(bidId);
    }
    @Override
    public TenderNoticePublishedEntity saveDraftTenderNoticeLog(Integer bidId, String fileName,String bidOpeningDate) {
        TenderInfo tender=tenderRepositoryImpl.viewTenderByTenderId(bidId);
        DraftTenderNoticeDto draftTenderNoticeDto = tenderRepositoryImpl.getDraftTenderNotice(bidId, bidOpeningDate);
        List<NoticeLevelMappingDto> noticeListing = tenderRepositoryImpl.getTenderLevelByNoticeId(draftTenderNoticeDto.getId());
        TenderNoticePublishedEntity tenderPublished=new TenderNoticePublishedEntity();

        if(tender.getBidId() != null){
            tenderPublished.setBidId(tender.getBidId());
            tenderPublished.setClosingDate(tender.getBidSubmissionDate());
            tenderPublished.setWorkName(draftTenderNoticeDto.getNameOfWork());
            tenderPublished.setDraftTenderNoticeDoc(fileName);
            tenderPublished.setActive(true);
            tenderPublished.setType(1);
            tenderPublished = tenderNoticePublishRepository.save(tenderPublished);
        } else {
            tenderPublished.setId(tender.getId());
            tenderPublished.setBidId(tender.getBidId());
            tenderPublished.setWorkName(tender.getNameOfWork());
            tenderPublished.setClosingDate(tender.getBidSubmissionDate());
            tenderPublished.setWorkName(draftTenderNoticeDto.getNameOfWork());
            tenderPublished.setDraftTenderNoticeDoc(fileName);
            tenderPublished.setActive(true);
            tenderPublished.setType(1);
            tenderPublished = tenderNoticePublishRepository.save(tenderPublished);
        }
        return tenderPublished;
//        TenderNoticePublishedEntity tenderPublished=new TenderNoticePublishedEntity();
//        tenderPublished.setBidId(tender.getBidId());
//        tenderPublished.setClosingDate(tender.getBidSubmissionDate());
//        tenderPublished.setWorkName(draftTenderNoticeDto.getNameOfWork());
////        tenderPublished.setDistId(noticeListing.get(0).getDistId());
//        tenderPublished.setDraftTenderNoticeDoc(fileName);
//        tenderPublished.setActive(true);
//        tenderPublished.setType(1);
//        return  tenderNoticePublishRepository.save(tenderPublished);
    }




    public static String convertToIndianCurrency(Double num) {
        BigDecimal bd = new BigDecimal(num);
        long number = bd.longValue();
        long no = bd.longValue();
        int decimal = (int) (bd.remainder(BigDecimal.ONE).doubleValue() * 100);
        int digits_length = String.valueOf(no).length();
        int i = 0;
        ArrayList<String> str = new ArrayList<>();
        HashMap<Integer, String> words = new HashMap<>();
        words.put(0, "");
        words.put(1, "One");
        words.put(2, "Two");
        words.put(3, "Three");
        words.put(4, "Four");
        words.put(5, "Five");
        words.put(6, "Six");
        words.put(7, "Seven");
        words.put(8, "Eight");
        words.put(9, "Nine");
        words.put(10, "Ten");
        words.put(11, "Eleven");
        words.put(12, "Twelve");
        words.put(13, "Thirteen");
        words.put(14, "Fourteen");
        words.put(15, "Fifteen");
        words.put(16, "Sixteen");
        words.put(17, "Seventeen");
        words.put(18, "Eighteen");
        words.put(19, "Nineteen");
        words.put(20, "Twenty");
        words.put(30, "Thirty");
        words.put(40, "Forty");
        words.put(50, "Fifty");
        words.put(60, "Sixty");
        words.put(70, "Seventy");
        words.put(80, "Eighty");
        words.put(90, "Ninety");
        String digits[] = {"", "Hundred", "Thousand", "Lakh", "Crore"};
        while (i < digits_length) {
            int divider = (i == 2) ? 10 : 100;
            number = no % divider;
            no = no / divider;
            i += divider == 10 ? 1 : 2;
            if (number > 0) {
                int counter = str.size();
                String plural = (counter > 0 && number > 9) ? "s" : "";
                String tmp = (number < 21) ? words.get(Integer.valueOf((int) number)) + " " + digits[counter] + plural : words.get(Integer.valueOf((int) Math.floor(number / 10) * 10)) + " " + words.get(Integer.valueOf((int) (number % 10))) + " " + digits[counter] + plural;
                str.add(tmp);
            } else {
                str.add("");
            }
        }

        Collections.reverse(str);
        String Rupees = String.join(" ", str).trim();

        String paise = (decimal) > 0 ? " And  " + words.get(Integer.valueOf((int) (decimal - decimal % 10))) + " " + words.get(Integer.valueOf((int) (decimal % 10))) : "";
        return   Rupees  ;
    }


    @Override
    public File preBidMeetingResult(HttpServletResponse exportResponse, Integer bidId, java.sql.Date bidOpeningDate, Integer userId) throws Exception {
        Context context = new Context();
        PreBidMeetingDto preBidMeetingDto = tenderRepositoryImpl.getDetailsOfPreBidMeeting(bidId,bidOpeningDate,userId);

        context.setVariable("preBidMeetingDto",preBidMeetingDto);
        String html = templateEngine.process("pre-bid-meeting", context);
        String pdfName = "preBidMeeting";
        return renderPdf(html, pdfName);
    }

    @Override
    public File getPackageWiseBidder(HttpServletResponse exportResponse, Integer bidId, java.sql.Date bidOpeningDate, Integer packageId, Integer userId) throws Exception {
        Context context = new Context();
        List<PackageWiseDto> packageWiseData =  tenderRepositoryImpl.getPackageWiseDetails(bidId, bidOpeningDate, packageId, userId) ;
        List<BlockBoundaryDto> blockName = tenderRepositoryImpl.getBlockNameDetails(bidId,bidOpeningDate,packageId);
        List<PackageWiseDto> projectIds = tenderRepositoryImpl.getProjectIds(bidId,bidOpeningDate,packageId);
        String projectIdsName =" ";

        for(int i=0; i<packageWiseData.size();i++)
        {
            String value  = tenderRepositoryImpl.getSimilarWorkValueRequired(bidId, bidOpeningDate, packageId);
            if (value != null)
            {
                packageWiseData.get(i).setSimilarWorkValueReq(value);
            }else {
                packageWiseData.get(i).setSimilarWorkValueReq("0.00");
            }

        }

        for(int k =0; k<projectIds.size();k++){
            String project = String.valueOf(projectIds.get(k).getProjectId());

            if (projectIds.get(k).getProjectId() != null) {
                if (!projectIdsName.contains(project)) {
                    if (k > 0) {
                        projectIdsName += ", ";
                    }

                    projectIdsName += project;
                }
            }

        }

        String record = "";
        Integer size = packageWiseData.size();
        if(size > 1)
        {
            record = size + " detail records";
        }
        else {
            record = size + " detail record";
        }
        context.setVariable("packageWiseBidderData",packageWiseData);
        context.setVariable("blockName",blockName);
        context.setVariable("record",record);
        context.setVariable("projectIdsName",projectIdsName);
        String html = templateEngine.process("package-wise-bidders", context);
        String pdfName = "packageWiseBidder";
        return renderPdf(html, pdfName);
    }

    @Override
    public File getFinancialBidPdf(HttpServletResponse exportResponse, Integer bidId, java.sql.Date technicalBidDate, Integer packageId, Integer userId) throws Exception {
        Context context = new Context();

        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        String str = formatter.format(date);

        Double maximumBid = tenderRepositoryImpl.getMaximumBidByTenderId(bidId);
        List<FinancialBidEvaluationDto> financialBidEvaluationDto = tenderRepositoryImpl.getFinancialBidDetails1(bidId,technicalBidDate,packageId);
        List<FinancialBidEvaluationDto> newList = new ArrayList<>();
        String workIdsName= " ";



        for(int i=0; i<financialBidEvaluationDto.size();i++)
        {
              if(financialBidEvaluationDto.get(i).getPercentageExcessLess() > 0)
              {
                 financialBidEvaluationDto.get(i).setExcessLess("Excess");

              }
              else {
                  financialBidEvaluationDto.get(i).setExcessLess("Less");

              }

              if(financialBidEvaluationDto.get(i).getReviewTechBidDate()==null)
              {
                  financialBidEvaluationDto.get(i).setReviewTechBidDate("  ");
              }



        }

        for(int j=0; j<financialBidEvaluationDto.size();j++)
        {
            if(financialBidEvaluationDto.get(j).getAwardType()==1 || financialBidEvaluationDto.get(j).getAwardType()==2 || financialBidEvaluationDto.get(j).getAwardType()==7)
            {

                if(financialBidEvaluationDto.get(j).getPercentageExcessLess() > 0)
                {
                    financialBidEvaluationDto.get(j).setExcess("Excess Over");

                } else {
                    financialBidEvaluationDto.get(j).setExcess("Less than");
                }

                if(financialBidEvaluationDto.get(j).getAddPerfSecurity().trim().equals(".00"))
                {
                    financialBidEvaluationDto.get(j).setAddPerfSecurity("0");
                }

                newList.add(financialBidEvaluationDto.get(j));
            }

        }

         List<FinancialBidEvaluationDto> financialBid = tenderRepositoryImpl.getBidderWiseFinancialBidDetails(bidId,newList.get(0).getAgencyId());
        for(int i=0;i<financialBid.size();i++){
            if(i==0){
            financialBid.get(i).setMaxBidCapacityPdfValue(tenderRepositoryImpl.getStringValue(financialBid.get(i).getMaxBidCapacity()));
            financialBid.get(i).setBalanceBidCapacityPdfValue(tenderRepositoryImpl.getStringValue(financialBid.get(i).getBalanceBidCapacity()));
           }
        else {
            financialBid.get(i).setMaxBidCapacity(financialBid.get(i - 1).getBalanceBidCapacity());
            financialBid.get(i).setBalanceBidCapacity(financialBid.get(i - 1).getBalanceBidCapacity() - (financialBid.get(i).getEstimated() * 2));
            financialBid.get(i).setMaxBidCapacityPdfValue(tenderRepositoryImpl.getStringValue(financialBid.get(i).getMaxBidCapacity()));
            financialBid.get(i).setBalanceBidCapacityPdfValue(tenderRepositoryImpl.getStringValue(financialBid.get(i).getBalanceBidCapacity()));
        }

        }

        for(int k =0; k<financialBid.size();k++){

            if (financialBid.get(k).getPackageId() != null) {
                if (!workIdsName.contains(financialBid.get(k).getPackageId())) {
                    if (k > 0) {
                        workIdsName += ", ";
                    }

                    workIdsName += financialBid.get(k).getPackageId();
                }
            }

        }

        Integer size = financialBid.size();
        String Nos = " ";
        if(size > 1)
        {
            Nos = size +" Nos.";
        }else {
            Nos = size + " No.";
        }





        context.setVariable("financialBidEvaluationDto",financialBidEvaluationDto);
        context.setVariable("str",str);
        context.setVariable("newList",newList);
        context.setVariable("maximumBid",maximumBid);
        context.setVariable("financialBid",financialBid);
        context.setVariable("Nos",Nos);
        context.setVariable("workIdsName",workIdsName);



        String html1 = templateEngine.process("financial-bid-pdf1", context);
        String pdfName1 = "FinancialBid";
        File file1 = renderPdf(html1, pdfName1);
        List<File> files = new ArrayList<>();
        files.add(file1);

        String mergedFileName = new ClassPathResource(PDF_RESOURCES).getFile().getPath() + "FinancialBid.pdf";


        File file = File.createTempFile("FinancialBid", ".pdf");
        OutputStream outputStream = new FileOutputStream(file);
        ITextRenderer renderer = new ITextRenderer(20f * 4f / 3f, 18);
        renderer.setDocumentFromString(html1, new ClassPathResource(PDF_RESOURCES).getFile().getPath());
        renderer.layout();
        renderer.createPDF(outputStream);
        outputStream.close();


        File mergedFile = new File(mergedFileName);
//        mergedFile.mkdir();

        PDFMergerUtility pdfMergerUtility = new PDFMergerUtility();
        pdfMergerUtility.setDestinationFileName(new ClassPathResource(PDF_RESOURCES).getFile().getPath() + "FinancialBid.pdf");
        pdfMergerUtility.addSource(file1);
//        pdfMergerUtility.addSource(file2);
//        pdfMergerUtility.addSource(file3);

        pdfMergerUtility.mergeDocuments(MemoryUsageSetting.setupMainMemoryOnly());
        return mergedFile;
    }


}










