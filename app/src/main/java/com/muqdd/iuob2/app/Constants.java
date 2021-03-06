package com.muqdd.iuob2.app;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Ali Yusuf on 4/8/2017.
 * iUOB-2
 */

@SuppressWarnings({"WeakerAccess","unused"})
public class Constants {
    private static Map<String, String> debCodeMap;

    public final static String SB_SECTIONS_LIST = "SB_SECTIONS_LIST";

    public final static List<String> collegesNameList = Arrays.asList("Arts", "Science", "Information Technology",
            "Business Administration", "Applied Studies", "Physical Education and Physiotherapy",
            "Engineering", "Bahrain Teachers College", "Health Sciences", "Law");

//            {"College of Arts", "College of Business Administration",
//            "College of Engineering", "College of Science", "College of Education", "French Studies Center",
//            "College of Information Technology", "College of Applied Studies", "College of Law",
//            "Bahrain Teachers College", "Physical Education and Physiotherapy", "College of Health Sciences",
//            "English Language Center", "Sharia'a", "Science  Research", "College of Physical Education",
//            "Confucius Institute", "Reserved", "Language Centers", "Confucius Institute", "Languages Institute",
//            "Unit for Teaching Excellence and Leadership"};

    public final static String[] coursesNameList = {"ACC","ACA","ACCA","ACCM","AH","ALH","AMST","ARAB","ARABA",
            "ARABM","ARCG","ART","BAA","BIOLS","BIONU","BIS","CEA","CEG","CENG","CGS","CHE","CHEMY",
            "CHENG","CHL","COM","CSA","CSC","DH","ECON","ECONA","ECONM","EDAR","EDEG","EDPS","EDTC",
            "EDU","EEDA","EEG","EENG","ELE","ENG","ENGG","ENGL",/*"ENGL.",*/"ENGLA","ENGLM","ENGLR","ENGLU",
            "EPD","ESD",/*"ESP.",*/"ETDA","EVALU","FA","FIN","FINA","FINM","FOUN","FREN","GEOG","GERM",
            "HIST","HISTO","HRLC","IEN","INTD","ISLM","IT","ITBIS","ITCE","ITCS","ITIS","JAPN","LAW","ITSE","ITNE",
            /*"LAW.",*/"LFS","MATHA","MATHS",/*"MATHS.",*/"MCM","MEDA","MEG","MENG","MGT","MGTA","MISA",
            "MKT","MKTA","MLS","MLT","MPHYS","NUR","OMA","PHA","PHAM","PHED","PHEDE","PHTY","PHYCS",
            "PHYCSA","PICDA","PICENG","PSYC","PSYCH","QM","RAD","SBF","SBS","SOCIO","STAT","STATA",
            "TC1AR","TC1ART","TC1EN","TC1IS","TC1MA","TC1MAT","TC1SC","TC1SCT","TC2AR","TC2ART",
            "TC2EN","TC2ENT","TC2IS","TC2IST","TC2MA","TC2MAT","TC2SC","TC2SCT","TCDE","TCDEE",
            "TCDEGS","TCDEIT","TCDEM","TCEL","TCFN","TCPB","TOUR","TRAN"};

    public final static String[] coursesList = {"ACC112","ACC113","ACC211","ACC221","ACC231","ACC310",
            "ACC311","ACC325","ACC332","ACC341","ACC410","ACC411","ACC416","ACC451","ACC470","ACC491",
            "ACC610","ACCA121","ACCA221","AMST202","AMST212","AMST213","AMST214","AMST411","AMST413",
            "ARAB100","ARAB110","ARAB111","ARAB119","ARAB141","ARAB181","ARAB210","ARAB212","ARAB213",
            "ARAB216","ARAB227","ARAB237","ARAB242","ARAB313","ARAB315","ARAB325","ARAB328","ARAB333",
            "ARAB334","ARAB337","ARAB338","ARAB339","ARAB341","ARAB343","ARAB353","ARAB414","ARAB416",
            "ARAB424","ARAB426","ARAB437","ARAB439","ARAB441","ARAB443","ARAB445","ARAB446","ARAB447",
            "ARAB448","ARAB454","ARAB488","ARABA111","ARABM535","ARABM548","ARABM552","ARABM558",
            "ARABM560","ARCG120","ARCG121","ARCG220","ARCG221","ARCG222","ARCG223","ARCG224","ARCG320",
            "ARCG321","ARCG322","ARCG323","ARCG420","ARCG421","ARCG422","ARCG520","ARCG521","ARCG522",
            "ARCG550","ARCG553","ARCG555","ARCG557","ARCG558","ARCG559","ARCG561","ART112","ART133",
            "ART141","ART221","BAA110","BAA120","BAA121","BAA122","BAA230","BAA231","BAA250","BAA251",
            "BAA260","BIOLS102","BIOLS103","BIOLS171","BIOLS175","BIOLS222","BIOLS232","BIOLS315",
            "BIOLS320","BIOLS352","BIOLS370","BIOLS371","BIOLS383","BIOLS409","BIOLS424","BIOLS427",
            "BIOLS442","BIOLS451","BIOLS454","BIOLS456","BIOLS463","BIOLS464","BIOLS465","BIOLS481",
            "BIS202","BIS315","CEA112","CEA122","CEA123","CEA124","CEA233","CEA242","CEG211","CEG225",
            "CEG315","CEG325","CENG131","CENG160","CENG200","CENG201","CENG202","CENG211","CENG212",
            "CENG231","CENG242","CENG290","CENG301","CENG302","CENG311","CENG312","CENG314","CENG321",
            "CENG322","CENG331","CENG341","CENG400","CENG406","CENG411","CENG415","CENG417","CENG421",
            "CENG426","CENG431","CENG435","CENG436","CENG442","CENG451","CENG491","CHEMY101","CHEMY102",
            "CHEMY106","CHEMY211","CHEMY220","CHEMY221","CHEMY223","CHEMY224","CHEMY310","CHEMY312",
            "CHEMY313","CHEMY322","CHEMY331","CHEMY341","CHEMY348","CHEMY351","CHEMY411","CHEMY421",
            "CHEMY435","CHEMYA101","CHENG111","CHENG211","CHENG212","CHENG213","CHENG214","CHENG242",
            "CHENG290","CHENG301","CHENG312","CHENG313","CHENG314","CHENG315","CHENG316","CHENG323",
            "CHENG324","CHENG325","CHENG400","CHENG415","CHENG421","CHENG422","CHENG423","CHENG425",
            "CHENG443","CHENG445","CHENG460","CHENG491","CHL101","COM524","COM525","COM526","COM540",
            "COM564","COM565","COM566","COM570","COM590","CSA101","CSA106","CSA111","CSA112","CSA113",
            "CSA114","CSA121","CSA126","CSA131","CSA136","CSA211","CSA212","CSA213","CSA214","CSA217",
            "CSA218","CSA219","CSA223","CSA231","CSA236","CSA241","CSA242","CSA266","CSA271","CSA276","CSC103",
            "ECON131","ECON140","ECON141","ECON248","ECON340","ECON341","ECON440","ECON441","ECON640",
            "ECONA121","EDAR126","EDEG211","EDPS241","EDPS285","EDTC100","EEDA101","EEDA102","EEDA109",
            "EEDA202","EEDA211","EEDA212","EEDA213","EEDA214","EEDA231","EEDA241","EEDA242","EEDA243",
            "EEDA244","EEDA280","EEG271","EEG510","EEG554","EEG577","EEG580","EENG100","EENG109",
            "EENG200","EENG204","EENG205","EENG209","EENG242","EENG251","EENG259","EENG261","EENG262",
            "EENG269","EENG271","EENG290","EENG301","EENG302","EENG311","EENG333","EENG334","EENG341",
            "EENG342","EENG343","EENG349","EENG352","EENG353","EENG355","EENG361","EENG364","EENG371",
            "EENG372","EENG373","EENG381","EENG400","EENG412","EENG413","EENG414","EENG415","EENG416",
            "EENG417","EENG433","EENG438","EENG444","EENG447","EENG448","EENG449","EENG451","EENG461",
            "EENG462","EENG463","EENG470","EENG472","EENG473","EENG479","EENG483","EENG485","EENG491",
            "ENGL101","ENGL102","ENGL111","ENGL112","ENGL114","ENGL125","ENGL126","ENGL130","ENGL135",
            "ENGL145","ENGL146","ENGL154","ENGL155","ENGL191","ENGL192","ENGL205","ENGL215","ENGL219",
            "ENGL235","ENGL250","ENGL305","ENGL308","ENGL309","ENGL313","ENGL314","ENGL315","ENGL331",
            "ENGL340","ENGL341","ENGL342","ENGL345","ENGL346","ENGL350","ENGL405","ENGL419","ENGL440",
            "ENGL444","ENGL446","ENGL450","ENGL540","ENGL541","ENGL542","ENGL543","ENGL544","ENGL545",
            "ENGLA111","ENGLA112","ENGLA120","ENGLA210","ENGLM401","ENGLM402","ENGLU203","ESD524",
            "ENGLR001", "ENGLR002", "ENGLR003", "ENGLR004", "ENGLR005", "ENGLR006",
            "ESD538","ETDA161","EVALU558","EVALU559","FIN220","FIN221","FIN222","FIN320","FIN323",
            "FIN331","FIN411","FIN424","FIN425","FIN426","FIN428","FIN435","FIN620","FIN623","FIN625",
            "FIN629","FIN698","FINA200","FOUN321","FREN141","FREN142","FREN231","FREN232","FREN310",
            "FREN312","FREN313","FREN411","GEOG102","GERM101","HIST122","HISTO191","HISTO212",
            "HISTO225", "HISTO230","HISTO281","HISTO301","HISTO302","HISTO305","HISTO306","HISTO309",
            "HISTO311","HISTO407","HISTO408","HISTO409","HISTO413","HISTO414","HISTO415","HISTO418",
            "HISTO419","HISTO429","IEN509","IEN511","IEN536","INTD120","INTD121","INTD220","INTD221",
            "INTD222","INTD223","INTD224","INTD320","INTD321","INTD322","INTD323","INTD324","INTD420",
            "INTD421","INTD422","INTD424","INTD425","ISLM101","ISLM114","ISLM136","ISLM141","ISLM217",
            "ISLM231","ISLM240","ISLM241","ISLM243","ISLM252","ISLM281","ISLM305","ISLM316","ISLM317",
            "ISLM321","ISLM323","ISLM326","ISLM327","ISLM333","ISLM339","ISLM344","ISLM346","ISLM401",
            "ISLM414","ISLM415","ISLM421","ISLM436","ISLM445","ISLM446","ISLM448","ISLM453","ISLM463",
            "ISLM474","ISLM475","ISLM488","ITBIS105","ITBIS211","ITBIS251","ITBIS311","ITBIS322",
            "ITBIS324","ITBIS341","ITBIS351","ITBIS373","ITBIS385","ITBIS393","ITBIS395","ITBIS396",
            "ITBIS420","ITBIS431","ITBIS435","ITBIS438","ITBIS445","ITBIS450","ITBIS465","ITBIS472",
            "ITBIS492","ITBIS494","ITBIS499","ITCE202","ITCE250","ITCE251","ITCE260","ITCE263",
            "ITCE272","ITCE300","ITCE311","ITCE313","ITCE314","ITCE315","ITCE320","ITCE321","ITCE341",
            "ITCE344","ITCE351","ITCE352","ITCE362","ITCE363","ITCE380","ITCE414","ITCE416","ITCE431",
            "ITCE436","ITCE444","ITCE455","ITCE470","ITCE471","ITCE472","ITCE474","ITCE498","ITCS102",
            "ITCS103","ITCS104","ITCS111","ITCS112","ITCS113","ITCS114","ITCS215","ITCS216","ITCS241",
            "ITCS242","ITCS251","ITCS252","ITCS253","ITCS311","ITCS312","ITCS314","ITCS315","ITCS322",
            "ITCS323","ITCS332","ITCS341","ITCS345","ITCS346","ITCS351","ITCS373","ITCS385","ITCS390",
            "ITCS393","ITCS395","ITCS399","ITCS412","ITCS420","ITCS439","ITCS447","ITCS452","ITCS473",
            "ITCS479","ITCS490","ITCS495","ITIS101","ITIS102","ITIS211","ITIS216","ITIS253","ITIS311",
            "ITIS312","ITIS313","ITIS314","ITIS331","ITIS341","ITIS342","ITIS343","ITIS351","ITIS411",
            "ITIS412","ITIS413","ITIS441","ITIS442","ITIS443","ITIS444","ITIS445","ITIS453","ITIS461",
            "ITIS462","ITIS464","ITIS475","ITIS476","ITIS482","ITIS499", "ITSE201", "ITNE110", "ITNE231",
            "JAPN101","LAW101","LAW102",
            "LAW104","LAW106","LAW107","LAW109","LAW110","LAW210","LAW211","LAW214","LAW215","LAW221",
            "LAW222","LAW224","LAW225","LAW227","LAW238","LAW302","LAW307","LAW312","LAW315","LAW317",
            "LAW318","LAW322","LAW324","LAW325","LAW327","LAW328","LAW402","LAW403","LAW407","LAW409",
            "LAW412","LAW414","LAW415","LAW416","LAW417","LAW418","LAW419","LAW422","LAW423","LAW425",
            "LAW427","LAW429","LAW499","LAW511","LAW513","LAW516","LAW522","LAW524","LAW529","MATHA111",
            "MATHS101","MATHS102","MATHS103","MATHS104","MATHS108","MATHS121","MATHS122","MATHS203",
            "MATHS204","MATHS205","MATHS211","MATHS253","MATHS303","MATHS304","MATHS305","MATHS307",
            "MATHS311","MATHS312","MATHS331","MATHS341","MATHS342","MATHS381","MATHS385","MATHS395",
            "MATHS401","MATHS402","MATHS415","MATHS417","MATHS452","MATHS461","MATHS500","MATHS562",
            "MATHS582","MCM110","MCM120","MCM130","MCM140","MCM150","MCM201","MCM202","MCM210","MCM220",
            "MCM230","MCM250","MCM260","MCM301","MCM302","MCM310","MCM311","MCM312","MCM313","MCM314",
            "MCM315","MCM316","MCM318","MCM320","MCM321","MCM322","MCM323","MCM324","MCM325","MCM330",
            "MCM331","MCM332","MCM333","MCM334","MCM336","MCM340","MCM341","MCM342","MCM345","MCM350",
            "MCM351","MCM352","MCM353","MCM354","MCM355","MCM356","MCM360","MCM361","MCM362","MCM401",
            "MCM410","MCM411","MCM412","MCM413","MCM414","MCM415","MCM420","MCM421","MCM422","MCM423",
            "MCM425","MCM430","MCM431","MCM432","MCM433","MCM440","MCM441","MCM442","MCM443","MCM450",
            "MCM451","MCM452","MCM453","MCM454","MCM460","MCM461","MEDA111","MEDA112","MEDA121",
            "MEDA161","MEDA162","MEDA213","MEDA214","MEDA215","MEDA222","MEDA223","MEDA231","MEDA232",
            "MEDA233","MEDA271","MEG435","MEG501","MEG514","MENG110","MENG160","MENG163","MENG201",
            "MENG210","MENG230","MENG231","MENG235","MENG242","MENG263","MENG274","MENG290","MENG300",
            "MENG310","MENG334","MENG335","MENG371","MENG373","MENG375","MENG380","MENG381","MENG384",
            "MENG400","MENG420","MENG423","MENG430","MENG440","MENG442","MENG473","MENG475","MENG485",
            "MENG490","MENG491","MGT131","MGT230","MGT233","MGT236","MGT239","MGT340","MGT341","MGT429",
            "MGT430","MGT433","MGT434","MGT437","MGT439","MGT446","MGT447","MGT460","MGT501","MGT630",
            "MGT632","MGT633","MGT635","MGT638","MGT639","MGT698","MGTA121","MGTA140","MGTA160",
            "MGTA222","MGTA231","MGTA240","MGTA242","MGTA247","MGTA251","MGTA260","MGTA262","MGTA264",
            "MGTA290","MGTA299","MISA121","MISA123","MISA138","MISA210","MISA233","MISA240","MISA244",
            "MISA260","MKT261","MKT263","MKT264","MKT268","MKT361","MKT362","MKT364","MKT367","MKT460",
            "MKT461","MKT462","MKT463","MKT464","MKT465","MKT660","MKTA221","MPHYS325","MPHYS372",
            "MPHYS374","MPHYS476","MPHYS477","OMA121","OMA140","OMA160","OMA222","OMA231","OMA240",
            "OMA242","OMA247","OMA260","OMA262","PHED555","PHED572","PHED577","PHED583","PHED584",
            "PHED704","PHED707","PHED709","PHED712","PHED724","PHEDE101","PHEDE102","PHEDE103",
            "PHEDE104","PHEDE106","PHEDE114","PHEDE115","PHEDE116","PHEDE117","PHEDE118","PHEDE200",
            "PHEDE201","PHEDE202","PHEDE203","PHEDE204","PHEDE205","PHEDE206","PHEDE207","PHEDE208",
            "PHEDE209","PHEDE210","PHEDE211","PHEDE213","PHEDE216","PHEDE217","PHEDE218","PHEDE301",
            "PHEDE302","PHEDE304","PHEDE305","PHEDE307","PHEDE308","PHEDE309","PHEDE310","PHEDE312",
            "PHEDE313","PHEDE314","PHEDE315","PHEDE317","PHEDE401","PHEDE402","PHEDE404","PHEDE411",
            "PHEDE418","PHEDE419","PHTY180","PHTY286","PHTY291","PHTY294","PHTY298","PHTY382",
            "PHTY384","PHTY387","PHTY388","PHTY394","PHTY396","PHTY494","PHTY499","PHYCS101",
            "PHYCS102","PHYCS104","PHYCS106","PHYCS111","PHYCS181","PHYCS209","PHYCS210","PHYCS221",
            "PHYCS222","PHYCS241","PHYCS324","PHYCS333","PHYCS334","PHYCS351","PHYCS353","PHYCS425",
            "PHYCS428","PHYCS432","PHYCS471","PHYCS484","PHYCS526","PHYCS541","PHYCS551","PHYCS553",
            "PHYCS554","PHYCS558","PHYCSA101","PICDA111","PICDA121","PICDA141","PICDA212","PICDA222",
            "PICDA223","PICDA224","PICDA225","PICDA226","PICDA231","PICDA232","PICDA233","PICENG111",
            "PICENG212","PICENG213","PICENG214","PICENG215","PICENG216","PICENG226","PICENG242",
            "PICENG290","PICENG321","PICENG322","PICENG325","PICENG326","PICENG331","PICENG332",
            "PICENG400","PICENG411","PICENG422","PICENG425","PICENG433","PICENG434","PICENG464",
            "PICENG491","PSYC103","PSYC120","PSYC211","PSYC221","PSYC224","PSYC251","PSYC290",
            "PSYC323","PSYC324","PSYC325","PSYC347","PSYC417","PSYCH561","PSYCH562","PSYCH563",
            "PSYCH570","PSYCH571","PSYCH591","QM250","QM350","QM353","QM650","SBF150","SBF250",
            "SBF260","SBF270","SOCIO161","SOCIO181","SOCIO191","SOCIO221","SOCIO222","SOCIO223",
            "SOCIO224","SOCIO225","SOCIO226","SOCIO281","SOCIO321","SOCIO324","SOCIO326","SOCIO328",
            "SOCIO329","SOCIO332","SOCIO333","SOCIO337","SOCIO341","SOCIO382","SOCIO420","SOCIO422",
            "SOCIO424","SOCIO425","SOCIO426","SOCIO427","SOCIO428","STAT105","STAT271","STAT272",
            "STAT273","STAT371","STAT372","STAT373","STAT374","STAT378","STAT381","STAT385","STAT391",
            "STAT392","STAT393","STAT394","STAT473","STAT474","STAT476","STAT479","STATA231","TOUR101",
            "TOUR102","TOUR211","TOUR220","TOUR231","TOUR315","TOUR355","TOUR363","TOUR380","TOUR418",
            "TOUR421","TOUR463","TOUR498","TRAN208","TRAN303","TRAN304","TRAN305","TRAN401","TRAN403",
            "TRAN404","HRLC107"
    };

    public static Map<String, String> getDebCodeMap() {
        if (debCodeMap == null) {
            debCodeMap = new HashMap<>();
            debCodeMap.put("ACC","031");
            debCodeMap.put("ACCA","A17");
            debCodeMap.put("ACCM","131");
            debCodeMap.put("AH","S20");
            debCodeMap.put("ALH","S19");
            debCodeMap.put("AMST","108");
            debCodeMap.put("ARAB","047");
            debCodeMap.put("ARABA","A24");
            debCodeMap.put("ARABM","147");
            debCodeMap.put("ARCG","292");
            debCodeMap.put("ART","079");
            debCodeMap.put("BAA","A40");
            debCodeMap.put("BIOLS","087");
            debCodeMap.put("BIONU","187");
            debCodeMap.put("BIS","058");
            debCodeMap.put("CEA","A25");
            debCodeMap.put("CEG","112");
            debCodeMap.put("CENG","325");
            debCodeMap.put("CGS","S25");
            debCodeMap.put("CHE","013");
            debCodeMap.put("CHEMY","086");
            debCodeMap.put("CHENG","353");
            debCodeMap.put("CHL","114");
            debCodeMap.put("COM","128");
            debCodeMap.put("CSA","A13");
            debCodeMap.put("CSC","081");
            debCodeMap.put("DH","S23");
            debCodeMap.put("ECON","034");
            debCodeMap.put("ECONA","A18");
            debCodeMap.put("ECONM","134");
            debCodeMap.put("EDAR","366");
            debCodeMap.put("EDEG","166");
            debCodeMap.put("EDPS","177");
            debCodeMap.put("EDTC","266");
            debCodeMap.put("EDU","S30");
            debCodeMap.put("EEDA","A29");
            debCodeMap.put("EEG","110");
            debCodeMap.put("EENG","345");
            debCodeMap.put("ELE","S12");
            debCodeMap.put("ENG","S28");
            debCodeMap.put("ENGG","377");
            debCodeMap.put("ENGL","049");
            debCodeMap.put("ENGL.","D11");
            debCodeMap.put("ENGLA","A11");
            debCodeMap.put("ENGLM","149");
            debCodeMap.put("ENGLU","009");
            debCodeMap.put("EPD","S26");
            debCodeMap.put("ESD","444");
            debCodeMap.put("ESP.","D16");
            debCodeMap.put("ETDA","A33");
            debCodeMap.put("EVALU","195");
            debCodeMap.put("FA","179");
            debCodeMap.put("FIN","032");
            debCodeMap.put("FINA","A21");
            debCodeMap.put("FINM","132");
            debCodeMap.put("FOUN","466");
            debCodeMap.put("FREN","078");
            debCodeMap.put("GEOG","076");
            debCodeMap.put("GERM","107");
            debCodeMap.put("HIST","075");
            debCodeMap.put("HISTO","175");
            debCodeMap.put("HRLC","771");
            debCodeMap.put("IEN","002");
            debCodeMap.put("INTD","191");
            debCodeMap.put("ISLM","074");
            debCodeMap.put("IT","558");
            debCodeMap.put("ITBIS","158");
            debCodeMap.put("ITCE","333");
            debCodeMap.put("ITCS","222");
            debCodeMap.put("ITIS","458");
            debCodeMap.put("JAPN","100");
            debCodeMap.put("LAW","080");
            debCodeMap.put("LAW.","808");
            debCodeMap.put("LFS","S24");
            debCodeMap.put("MATHA","A12");
            debCodeMap.put("MATHS","083");
            debCodeMap.put("MATHS.","D15");
            debCodeMap.put("MCM","228");
            debCodeMap.put("MEDA","A30");
            debCodeMap.put("MEG","111");
            debCodeMap.put("MENG","314");
            debCodeMap.put("MGT","030");
            debCodeMap.put("MGTA","A15");
            debCodeMap.put("MISA","A16");
            debCodeMap.put("MKT","033");
            debCodeMap.put("MKTA","A20");
            debCodeMap.put("MLS","S32");
            debCodeMap.put("MLT","S21");
            debCodeMap.put("MPHYS","285");
            debCodeMap.put("NUR","S11");
            debCodeMap.put("OMA","A60");
            debCodeMap.put("PHA","S18");
            debCodeMap.put("PHAM","S31");
            debCodeMap.put("PHED","001");
            debCodeMap.put("PHEDE","200");
            debCodeMap.put("PHTY","220");
            debCodeMap.put("PHYCS","085");
            debCodeMap.put("PHYCSA","A31");
            debCodeMap.put("PICDA","A34");
            debCodeMap.put("PICENG","355");
            debCodeMap.put("PSYC","077");
            debCodeMap.put("PSYCH","277");
            debCodeMap.put("QM","035");
            debCodeMap.put("RAD","S22");
            debCodeMap.put("SBF","777");
            debCodeMap.put("SBS","S27");
            debCodeMap.put("SOCIO","173");
            debCodeMap.put("STAT","096");
            debCodeMap.put("STATA","A19");
            debCodeMap.put("TC1AR","E27");
            debCodeMap.put("TC1ART","E40");
            debCodeMap.put("TC1EN","E28");
            debCodeMap.put("TC1IS","E44");
            debCodeMap.put("TC1MA","E25");
            debCodeMap.put("TC1MAT","E39");
            debCodeMap.put("TC1SC","E26");
            debCodeMap.put("TC1SCT","E24");
            debCodeMap.put("TC2AR","E35");
            debCodeMap.put("TC2ART","E36");
            debCodeMap.put("TC2EN","E33");
            debCodeMap.put("TC2ENT","E34");
            debCodeMap.put("TC2IS","E37");
            debCodeMap.put("TC2IST","E38");
            debCodeMap.put("TC2MA","E32");
            debCodeMap.put("TC2MAT","E42");
            debCodeMap.put("TC2SC","E31");
            debCodeMap.put("TC2SCT","E41");
            debCodeMap.put("TCDE","E12");
            debCodeMap.put("TCDEE","E18");
            debCodeMap.put("TCDEGS","E14");
            debCodeMap.put("TCDEIT","E23");
            debCodeMap.put("TCDEM","E13");
            debCodeMap.put("TCEL","E45");
            debCodeMap.put("TCFN","E55");
            debCodeMap.put("TCPB","E11");
            debCodeMap.put("TOUR","027");
            debCodeMap.put("TRAN","082");
        }
        return debCodeMap;
    }

    public static String getDebCode(String k){
        return getDebCodeMap().get(k);
    }
}
