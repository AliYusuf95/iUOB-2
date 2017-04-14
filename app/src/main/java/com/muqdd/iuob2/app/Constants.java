package com.muqdd.iuob2.app;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ali Yusuf on 4/8/2017.
 * iUOB-2
 */

public class Constants {
    private static Map<String, String> debCodeMap;

    public final static String[] coursesList = {"ACC","ACCA","ACCM","AH","ALH","AMST","ARAB","ARABA",
            "ARABM","ARCG","ART","BAA","BIOLS","BIONU","BIS","CEA","CEG","CENG","CGS","CHE","CHEMY",
            "CHENG","CHL","COM","CSA","CSC","DH","ECON","ECONA","ECONM","EDAR","EDEG","EDPS","EDTC",
            "EDU","EEDA","EEG","EENG","ELE","ENG","ENGG","ENGL",/*"ENGL.",*/"ENGLA","ENGLM","ENGLU",
            "EPD","ESD",/*"ESP.",*/"ETDA","EVALU","FA","FIN","FINA","FINM","FOUN","FREN","GEOG","GERM",
            "HIST","HISTO","HRLC","IEN","INTD","ISLM","IT","ITBIS","ITCE","ITCS","ITIS","JAPN","LAW",
            /*"LAW.",*/"LFS","MATHA","MATHS",/*"MATHS.",*/"MCM","MEDA","MEG","MENG","MGT","MGTA","MISA",
            "MKT","MKTA","MLS","MLT","MPHYS","NUR","OMA","PHA","PHAM","PHED","PHEDE","PHTY","PHYCS",
            "PHYCSA","PICDA","PICENG","PSYC","PSYCH","QM","RAD","SBF","SBS","SOCIO","STAT","STATA",
            "TC1AR","TC1ART","TC1EN","TC1IS","TC1MA","TC1MAT","TC1SC","TC1SCT","TC2AR","TC2ART",
            "TC2EN","TC2ENT","TC2IS","TC2IST","TC2MA","TC2MAT","TC2SC","TC2SCT","TCDE","TCDEE",
            "TCDEGS","TCDEIT","TCDEM","TCEL","TCFN","TCPB","TOUR","TRAN"};

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
