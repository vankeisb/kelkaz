package agregator.util;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;

public enum ServiceLocalisation {

    INSTANCE();

    private List<String[]> postCodes;

    private ServiceLocalisation() {
        CSVReader reader;
        try {
            FileReader fileReader = new FileReader("src/main/resources/FR_cities.csv");
            reader = new CSVReader(fileReader, ';');
            postCodes = reader.readAll();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public String getCityByPostCode(String postCode){
        for (String[] localisation : postCodes) {
            if (localisation[1].equals(postCode))
                return localisation[0];
        }
        return null;
    }

    public String getCityByCityName(String city){
        city = city.toUpperCase();
        city = city.trim();
        System.out.println("City to threat = " + city);
        for (String[] localisation : postCodes) {
            if (localisation[0].equals(city))
                return localisation[0];
        }
        return null;
    }

    public String getPostCodeByCity(String city){
        city = city.toUpperCase();
        city = city.trim();
        for (String[] localisation : postCodes) {
            if (localisation[0].equals(city))
                return localisation[1];
        }
        return null;
    }

    public String getDepartmentByPostCode(String postCode){
        for (String[] localisation : postCodes) {
            if (localisation[1].equals(postCode))
                return localisation[2];
        }
        return null;
    }

    public String getDepartmentByCity(String city){
        city = city.toUpperCase();
        city = city.trim();
        for (String[] localisation : postCodes) {
            if (localisation[0].equals(city))
                return localisation[2];
        }
        return null;
    }

    public String getNbDepartmentByDepartment(String department){
        department = department.toUpperCase();
        department = department.trim();
        for (String[] localisation : postCodes) {
            if (localisation[2].equals(department)){
                return localisation[1].substring(0, 2);
            }
        }
        return null;
    }

    public String getDepartmentByNbDepartment(String nb){
        for (String[] localisation : postCodes) {
            if (localisation[1].substring(0, 2).equals(nb)){
                return localisation[2];
            }
        }
        return null;
    }

    public String[] getLocalisation(String criteria){
        String[] resultsNotSorted = criteria.split(",");
        for (String string : resultsNotSorted) {
            System.out.println(string);
        }
        String[] results = new String[3];
        for (String c : resultsNotSorted) {
            c = c.trim();
            if (c.length() == 2 && c.matches("[0-9]*")){
                // department number
                results[2] = getDepartmentByNbDepartment(c);
                results[1] = c+"000";
            }else{
                if (c.length() > 2 && c.matches("[0-9]*")){
                    // post code
                    results[1] = c;
                    results[0] = getCityByPostCode(c);
                    results[2] = getDepartmentByPostCode(c);
                }else{
                    String city = getCityByCityName(c);
                    System.out.println("City = " + city);
                    results[0] = city;
                    results[1] = getPostCodeByCity(city);
                    results[2] = getDepartmentByCity(city);
                }
            }
        }
        return results;
    }
}
