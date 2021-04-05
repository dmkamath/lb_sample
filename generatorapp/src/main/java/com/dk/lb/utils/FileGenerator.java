package com.dk.lb.utils;

import com.dk.lb.model.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class FileGenerator {

    // 10, 10, 10by10
    // 20, 20, 20by20
    // 400, 7000, 4by7
    private static final long HOTEL_ID_COUNT = 2;
    private static final long PER_HOTEL_ID_KIOSK_COUNT = 3;
    private static final String KIOSK_OUT = "lockbox_multi_set_small.json";
    private static final String HOTEL_CONFIG_OUT = "hotel_config_small.json";
    private static final String HOTEL_PAYMENTS_OUT = "hotel_payments_small.json";
    private static final String HOTEL_CONFIG_OUT_PREFIX = "hotel_config_";
    private static final String HOTEL_PAYMENTS_OUT_PREFIX = "hotel_payments_";

    private static final String IN_PATH = "C:\\gitrepos\\lb_sample\\generatorapp\\src\\main\\resources\\";
    private static final String OUT_PATH = "C:\\gitrepos\\resources_out\\";
    private static StringGenerator stringGenerator = new StringGenerator();
    private static long currentBatchId = 5000;
    private static String currentDateStr = "01/01/2020";
    private static Date currentDate = null;
    private static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy");
    private static SimpleDateFormat DATE_FORMAT_FILE_NAME = new SimpleDateFormat("yyyy_MM_dd");
    private static List<String> PAYMENT_STANDARD_FIELDS = Arrays.asList("routingNumber", "accountNumber", "checkNumber", "amount", "remitterName", "micr");
    private static List<String> INVOICE_STANDARD_FIELDS = Arrays.asList("invoiceNumber", "scanLine", "arSystemRef");

    public static  void main (String[] args) throws IOException {
        initialize();
        //generateByTemplate();
        generateConfigAndTransaction(1, 2, 125, "small3", true);
    }

    private static void initialize() {
        try {
            currentDate = DATE_FORMAT.parse(currentDateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private static void addDays(int dayIdx) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentDate);
        cal.add(Calendar.DATE, dayIdx);
        currentDate = cal.getTime();
        currentDateStr = DATE_FORMAT.format(cal.getTime());
    }

    private static String getCurrentDateForFilename() {
        return DATE_FORMAT_FILE_NAME.format(currentDate);
    }

    // each hotel has 10 kiosks
    // each kiosk has fixed number of custom fields
    private static void generateConfigAndTransaction(int hotelCount, int days, int dailyRentals, String fileSuffix, boolean isWriteByDay) throws IOException {
        String msg = "generating hotelCount=" + hotelCount
                + ", days=" + days
                + ", dailyRentals=" + dailyRentals;
        Timer t = new Timer(msg);
        List<Hotel> hotelList = new ArrayList<>();
        List<Kiosk> kioskList = new ArrayList<>();

        System.out.println("Started generating hotel config");
        for (int hotelIdx = 0; hotelIdx < hotelCount; hotelIdx++) {
            long hotelId = 1000 + hotelIdx;
            Hotel hotel = generateHotelData(hotelId);
            hotelList.add(hotel);
        }
        writeHotelConfig(hotelList, fileSuffix);
        System.out.println("Done generating hotel config");

        System.out.println("Started generating hotel payments");
        long itemCount = 0;
        for (int dayIdx = 0; dayIdx < days; dayIdx++) {
            for (Hotel hotel : hotelList) {
                Kiosk kiosk = generateKioskData(hotel, dailyRentals);
                kioskList.add(kiosk);
                itemCount += kiosk.getInvoiceCount();
                itemCount += kiosk.getPaymentCount();
            }
            if(isWriteByDay) {
                writeHotelPayments(kioskList, fileSuffix, isWriteByDay);
                kioskList = new ArrayList<>();
                System.out.println("Done generating hotel payments for dayIdx="+dayIdx + " of "+days + " days");
            }
            addDays(1);
        }
        if(!isWriteByDay) {
            writeHotelPayments(kioskList, fileSuffix, isWriteByDay);
        }
        t.stop();
        System.out.println("Done generating hotel payments");
        t.log(" with itemCount="+itemCount);
    }

    private static Kiosk generateKioskData(Hotel hotel, int dailyRentals) {
        Kiosk kiosk = new Kiosk();

        kiosk.setKioskId(currentBatchId++);
        kiosk.setDepositDate(currentDateStr);
        kiosk.setHotelId(hotel.getHotelId());
        Rental rental = new Rental();
        List<Payment> payments = new ArrayList<>();
        List<Invoice> invoices = new ArrayList<>();
        long kioskAmount = 0;
        for (int rentalId = 1; rentalId <= dailyRentals; rentalId++) {
            Payment payment = new Payment();
            payment.setRentalId(rentalId);
            payment.setRoutingNumber(stringGenerator.generateLong(9));
            payment.setAccountNumber(stringGenerator.generateLong(10));
            payment.setCheckNumber(stringGenerator.generateLong(6));
            payment.setAmount(stringGenerator.generateLong(2));
            payment.setMicr(stringGenerator.generate(15, true));
            payment.setRemitterName(stringGenerator.generatePersonName());
            Map<String, String> customFieldsWithValues = new HashMap<>();
            for (String field: getCustomFields(hotel, true)) {
                String value = stringGenerator.genarateSimpleEnglishWord();
                customFieldsWithValues.put(field, value);
            }
            payment.setCustomFields(customFieldsWithValues);
            payments.add(payment);

            Invoice invoice = new Invoice();
            invoice.setRentalId(rentalId);
            invoice.setInvoiceNumber(stringGenerator.generateLong(12));
            invoice.setScanLine(stringGenerator.generateLong(13));
            invoice.setArSystemRef(stringGenerator.generateLong(14));
            invoice.setAmount(payment.getAmount());
            customFieldsWithValues = new HashMap<>();
            for (String field: getCustomFields(hotel, false)) {
                String value = stringGenerator.genarateSimpleEnglishWord();
                customFieldsWithValues.put(field, value);
            }
            invoice.setCustomFields(customFieldsWithValues);
            invoices.add(invoice);

            kioskAmount += payment.getAmount();
        }
        kiosk.setPaymentCount(dailyRentals);
        kiosk.setInvoiceCount(dailyRentals);
        kiosk.setKioskAmount(kioskAmount);
        rental.setPayments(payments);
        rental.setInvoices(invoices);
        kiosk.setRental(rental);
        return kiosk;
    }

    private static List<String> getCustomFields(Hotel hotel, boolean isPayment) {
        List<String> customFields = new ArrayList<>();
        for (Document doc : hotel.getDocumentList()) {
            if (isPayment && doc.getDocumentType() == 2) {
                for (String field : doc.getCustomFields()) {
                    if (!PAYMENT_STANDARD_FIELDS.contains(field)) {
                        customFields.add(field);
                    }
                }
            } else if (!isPayment && doc.getDocumentType() == 1) {
                for (String field : doc.getCustomFields()) {
                    if (!INVOICE_STANDARD_FIELDS.contains(field)) {
                        customFields.add(field);
                    }
                }
            }
        }
        return customFields;
    }

    private static Hotel generateHotelData(long hotelId) {
        Hotel hotel = new Hotel();
        hotel.setHotelId(hotelId);
        hotel.setHotelName(stringGenerator.generateCompanyName());

        List<Document> documentList = new ArrayList<>();

        Document invoiceDocument = new Document();
        invoiceDocument.setDocumentId(10);
        invoiceDocument.setDocumentType(1);
        List<String> iCustomFields = new ArrayList<>();
        iCustomFields.add("invoiceNumber");
        iCustomFields.add("scanLine");
        iCustomFields.add("arSystemRef");
        iCustomFields.add(stringGenerator.genarateSimpleEnglishWord());
        iCustomFields.add(stringGenerator.genarateSimpleEnglishWord());
        invoiceDocument.setCustomFields(iCustomFields);
        documentList.add(invoiceDocument);

        Document paymentDocument = new Document();
        paymentDocument.setDocumentId(10);
        paymentDocument.setDocumentType(2);
        List<String> pCustomFields = new ArrayList<>();
        pCustomFields.add("routingNumber");
        pCustomFields.add("accountNumber");
        pCustomFields.add("checkNumber");
        pCustomFields.add("amount");
        pCustomFields.add("remitterName");
        pCustomFields.add("micr");
        pCustomFields.add(stringGenerator.genarateSimpleEnglishWord());
        pCustomFields.add(stringGenerator.genarateSimpleEnglishWord());
        paymentDocument.setCustomFields(pCustomFields);
        documentList.add(paymentDocument);

        hotel.setDocumentList(documentList);

        return hotel;
    }

    public static  void generateByTemplate () throws IOException {
        Kiosk templateKiosk = readTemplateBatch();
        long kioskCountPerHotel = PER_HOTEL_ID_KIOSK_COUNT;
        List<Kiosk> clonedKioskList = cloneHotel(templateKiosk, kioskCountPerHotel);
        writeKioskList(clonedKioskList);
    }

    private static Kiosk readTemplateBatch() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String filename = IN_PATH + "hotel_data.json";
        Kiosk kiosk = mapper.readValue(new File(filename), Kiosk.class);
        return kiosk;
    }

    private static List<Kiosk> cloneHotel(Kiosk templateKiosk, long kioskCountPerHotel) {
        List<Kiosk> list = new ArrayList<>();
        long hotelId = templateKiosk.getHotelId();
        for (int cidx = 0; cidx < HOTEL_ID_COUNT; cidx++) {
            list.addAll(cloneKiosk(templateKiosk, hotelId+cidx, kioskCountPerHotel));
            System.out.println("done with hotelId="+(hotelId+cidx));
        }
        return list;
    }

    private static List<Kiosk> cloneKiosk(Kiosk templateKiosk, long hotelId, long batchCountPerBox) {
        List<Kiosk> list = new ArrayList<>();
        for (int i=1; i<=batchCountPerBox; i++) {
            Kiosk clone = new Kiosk();
            String batchId = String.format("%04d%04d", hotelId, i);
            //System.out.println("batchId="+batchId);
            clone.setHotelId(hotelId);
            clone.setKioskId(Long.valueOf(batchId));
            clone.setDepositDate(templateKiosk.getDepositDate());
            clone.setKioskAmount(templateKiosk.getKioskAmount());
            clone.setPaymentCount(templateKiosk.getPaymentCount());
            clone.setInvoiceCount(templateKiosk.getInvoiceCount());

            Rental txn = new Rental();
            List<Payment> payments = new ArrayList<>();
            for (Payment tc : templateKiosk.getRental().getPayments()) {
                Payment nc = new Payment();
                String trn = "" + tc.getRoutingNumber();
                String nrn = trn.replace("10010001", batchId);
                //System.out.println("nrn="+nrn);
                nc.setRoutingNumber(Long.valueOf(nrn));

                String tan = "" + tc.getAccountNumber();
                String nan = tan.replace("10010001", batchId);
                //System.out.println("nan="+nan);
                nc.setAccountNumber(Long.valueOf(nan));

                String tcn = "" + tc.getCheckNumber();
                String ncn = tcn.replace("10010001", batchId);
                //System.out.println("ncn="+ncn);
                nc.setCheckNumber(Long.valueOf(ncn));

                nc.setAmount(tc.getAmount());
                nc.setRentalId(tc.getRentalId());

                String remitterName = stringGenerator.generatePersonName();
                nc.setRemitterName(remitterName);
                String micrValue = stringGenerator.generate(15, true);
                nc.setMicr(micrValue);
                payments.add(nc);
            }
            txn.setPayments(payments);

            List<Invoice> invoices = new ArrayList<>();
            for (Invoice tc : templateKiosk.getRental().getInvoices()) {
                Invoice nc = new Invoice();
                String tin = "" + tc.getInvoiceNumber();
                String nin = tin.replace("10010001", batchId);
                //System.out.println("nin="+nin);
                nc.setInvoiceNumber(Long.valueOf(nin));

                String tsl = "" + tc.getScanLine();
                String nsl = tsl.replace("10010001", batchId);
                //System.out.println("nsl="+nsl);
                nc.setScanLine(Long.valueOf(nsl));

                String tas = "" + tc.getArSystemRef();
                String nas = tas.replace("10010001", batchId);
                //System.out.println("ncn="+nas);
                nc.setArSystemRef(Long.valueOf(nas));

                nc.setAmount(tc.getAmount());
                nc.setRentalId(tc.getRentalId());
                invoices.add(nc);
            }
            txn.setInvoices(invoices);
            clone.setRental(txn);
            list.add(clone);
        }
        return list;
    }

    private static void writeKioskList(List<Kiosk> clondeKioskList) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String filename = OUT_PATH + KIOSK_OUT;
        mapper.writerWithDefaultPrettyPrinter().writeValue(new File(filename), clondeKioskList);
    }

    private static void writeHotelPayments(List<Kiosk> hotelList, String fileSuffix, boolean isWriteByDay) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        StringBuilder filename = new StringBuilder();
        filename.append(OUT_PATH).append(HOTEL_PAYMENTS_OUT_PREFIX).append(fileSuffix);
        if (isWriteByDay) {
            filename.append("_");
            filename.append(getCurrentDateForFilename());
        }
        filename.append(".json");
        mapper.writerWithDefaultPrettyPrinter().writeValue(new File(filename.toString()), hotelList);
    }

    private static void writeHotelConfig(List<Hotel> hotel, String fileSuffix) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String filename = OUT_PATH + HOTEL_CONFIG_OUT_PREFIX + fileSuffix + ".json";
        mapper.writerWithDefaultPrettyPrinter().writeValue(new File(filename), hotel);
    }

    public static  void simpleTest (String[] args) {
        System.out.println("hello world");

        Kiosk b = new Kiosk();
        b.setKioskId(111);
        b.setPaymentCount(1234);
        Rental txn = new Rental();
        List<Payment> payments = new ArrayList<>();
        Payment payment = new Payment();
        payment.setCheckNumber(111);
        payments.add(payment);
        txn.setPayments(payments);
        b.setRental(txn);

        ObjectMapper mapper = new ObjectMapper();
        try {
            String filename = "C:\\gitrepos\\lb_sample\\generatorapp\\src\\main\\resources\\lockbox_out.json";
            String jsonString = mapper.writeValueAsString(b);
            System.out.println("sample batch is "+jsonString);
            List<Kiosk> lb = new ArrayList<>();
            lb.add(b);
            lb.add(b);

            mapper.writeValue(new File(filename), lb);

            filename = "C:\\gitrepos\\lb_sample\\generatorapp\\src\\main\\resources\\hotel_data.json";
            Kiosk kiosk = mapper.readValue(new File(filename), Kiosk.class);
            System.out.println("read real batch:"+ kiosk);


        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
