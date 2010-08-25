package agregator.core.util;

import java.util.List;

import agregator.util.ServiceLocalisation;
import junit.framework.TestCase;

public class TestServiceLocalisation extends TestCase{

	public void testGetCityByPostCode(){
		String postCode = "06370";
		String city = ServiceLocalisation.INSTANCE.getCityByPostCode(postCode);
		assertEquals("MOUANS SARTOUX", city);
	}
	
	public void testGetPostCodeByCity(){
		String city = "MOUANS SARTOUX";
		String postCode = ServiceLocalisation.INSTANCE.getPostCodeByCity(city);
		assertEquals("06370", postCode);
		
		String cityInLowerCase = "Mouans Sartoux";
		String postCodeLC = ServiceLocalisation.INSTANCE.getPostCodeByCity(cityInLowerCase);
		assertEquals("06370", postCodeLC);
		
	}
	
	public void testGetDepartmentByPostCode(){
		String postCode = "06370";
		String dep = ServiceLocalisation.INSTANCE.getDepartmentByPostCode(postCode);
		assertEquals("ALPES MARITIMES", dep);
	}
	
	public void testGetDepartmentByCity(){
		String city = "mouans sartoux";
		String dep = ServiceLocalisation.INSTANCE.getDepartmentByCity(city);
		assertEquals("ALPES MARITIMES", dep);
	}
	
	public void testGetNbDepartmentByDepartment(){
		String dep = ServiceLocalisation.INSTANCE.getDepartmentByPostCode("06370");
		String nb = ServiceLocalisation.INSTANCE.getNbDepartmentByDepartment(dep);
		assertEquals("06", nb);
	}
	
	public void testGetDepartmentByNbDepartment(){
		String dep = ServiceLocalisation.INSTANCE.getDepartmentByNbDepartment("06");
		assertEquals("ALPES MARITIMES", dep);
	}
	
	public void testGetLocalisation(){
		String criteria = "06, Mouans sartoux";
		String[] results = ServiceLocalisation.INSTANCE.getLocalisation(criteria);
		
		assertEquals("MOUANS SARTOUX", results[0]);
		assertEquals("06370", results[1]);
		assertEquals("ALPES MARITIMES", results[2]);
		
		criteria = "Mouans sartoux";
		results = ServiceLocalisation.INSTANCE.getLocalisation(criteria);
		
		assertEquals("MOUANS SARTOUX", results[0]);
		assertEquals("06370", results[1]);
		assertEquals("ALPES MARITIMES", results[2]);
		
		criteria = "06370";
		results = ServiceLocalisation.INSTANCE.getLocalisation(criteria);
		
		assertEquals("MOUANS SARTOUX", results[0]);
		assertEquals("06370", results[1]);
		assertEquals("ALPES MARITIMES", results[2]);
		
		criteria = "06";
		results = ServiceLocalisation.INSTANCE.getLocalisation(criteria);

		assertEquals("ALPES MARITIMES", results[2]);
	}
}
