package com.prac.HapicFhirBasicProject;

import org.hl7.fhir.instance.model.api.IIdType;
import org.hl7.fhir.r4.model.DateType;
import org.hl7.fhir.r4.model.Enumerations;
import org.hl7.fhir.r4.model.Patient;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.DataFormatException;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.parser.StrictErrorHandler;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;

/**
 * imp urls: https://github.com/hapifhir/hapi-fhir
 * https://fhir.docs.careevolution.com/overview/requests.html
 * https://hapifhir.io/hapi-fhir/docs/client/examples.html http://hapi.fhir.org/
 * https://github.com/hapifhir/hapi-fhir/tree/master/hapi-fhir-spring-boot/hapi-fhir-spring-boot-samples
 *
 */
public class App {
	/**
	 * This is the Java main method, which gets executed test
	 */
	public static void main(String[] args) {

		// Create a context
		FhirContext ctx = FhirContext.forR4();

		// Create a client
		IGenericClient client = ctx.newRestfulGenericClient("https://hapi.fhir.org/baseR4");

		// Read a patient with the given ID
//	      step1_read_a_resource(ctx, client);
//	      step2_search_for_patients_named_test(ctx, client);
//	      step3_create_patient();
		validate();

	}

	public static void step1_read_a_resource(FhirContext ctx, IGenericClient client) {

		Patient patient;
		try {
			// Try changing the ID from 952975 to 999999999999
//				patient = client.read().resource(Patient.class).withId("1710334").execute();
			patient = client.read().resource(Patient.class).withId("2367999").execute();
		} catch (ResourceNotFoundException e) {
			System.out.println("Resource not found!");
			return;
		}

		String string = ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(patient);
		System.out.println(string);

	}

	public static void step2_search_for_patients_named_test(FhirContext ctx, IGenericClient client) {

		org.hl7.fhir.r4.model.Bundle results = client.search().forResource(Patient.class)
				.where(Patient.NAME.matches().value("test")).returnBundle(org.hl7.fhir.r4.model.Bundle.class).execute();

		System.out.println("First page: ");
		System.out.println(ctx.newJsonParser().encodeResourceToString(results));

		// Load the next page
		org.hl7.fhir.r4.model.Bundle nextPage = client.loadPage().next(results).execute();

		System.out.println("Next page: ");
		System.out.println(ctx.newJsonParser().encodeResourceToString(nextPage));

	}

	public static void step3_create_patient() {
		// Create a patient
		Patient newPatient = new Patient();

		// Populate the patient with fake information
		newPatient.addName().setFamily("DevDays2015").addGiven("John").addGiven("Q");
		newPatient.addIdentifier().setSystem("http://acme.org/mrn").setValue("1234567");
		newPatient.setGender(Enumerations.AdministrativeGender.MALE);
		newPatient.setBirthDateElement(new DateType("2015-11-18"));

		// Create a client
		FhirContext ctx = FhirContext.forR4();
		IGenericClient client = ctx.newRestfulGenericClient("http://hapi.fhir.org/baseR4");

		// Create the resource on the server
		MethodOutcome outcome = client.create().resource(newPatient).execute();

		// Log the ID that the server assigned
		IIdType id = outcome.getId();
		System.out.println("Created patient, got ID: " + id);
	}

	// TODO

	public static void validate() {
		FhirContext ctx = FhirContext.forR4();

		// Create a parser and configure it to use the strict error handler
		// Instantiate a new parser
		
		IParser parser = ctx.newJsonParser();
		parser.setParserErrorHandler(new StrictErrorHandler());

		String input = "{" + "\"resourceType\" : \"Patient\"," + "  \"name\" : [{" + "    \"family\": \"Simpson\""
				+ "  }]" + "}";

		// The following will throw a DataFormatException if parsing fails
		// StrictErrorHandler
		try {
			// Parse it
			Patient parsed = parser.parseResource(Patient.class, input);
			System.out.println(parsed.getName().get(0).getFamily());
			System.out.println(parsed.getName().get(0).getId());	// will print null as id does not exists
		} catch (DataFormatException e) {
			System.out.println(e.getMessage());
		}
	}
}
