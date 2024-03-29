package com.healthcare.healthcaremanagement.integration;

import com.healthcare.healthcaremanagement.enumerator.ErrorMessages;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.ResourceUtils;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Integration for API test implementation.
 */
@ActiveProfiles("test")
@Sql({"/sql/purge.sql", "/sql/seed.sql"})
@RunWith(SpringRunner.class)
@SuppressWarnings({"PMD.TooManyMethods", "checkstyle:AbbreviationAsWordInName"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class HealthCareManagementIntegrationTests {

    private static final String BASE_ENDPOINT = "http://localhost:8090/healthcare-management";
    private static final String EXAM = "/exam/";
    private static final String INSTITUTION = "/healthCareInstitution/";
    private static final String USER = "/user/";
    private static final String INSERT_EXAM_SUCCESS = "request/insertExamSuccess.json";

    private final TestRestTemplate testRestTemplate = new TestRestTemplate("admin@admin", "admin");

    private String payload;

    private HttpEntity<String> entity;

    private ResponseEntity<String> response;

    private String examId;

    /**
     * Read json.
     *
     * @param filename file name input
     * @return String file content
     */
    private static String readJson(final String filename) {
        try {
            return FileUtils.readFileToString(ResourceUtils.getFile("classpath:" + filename), "UTF-8");
        } catch (IOException exception) {
            return null;
        }
    }

    /**
     * Build Http headers.
     *
     * @return Http Headers object
     */
    private HttpHeaders buildHttpHeaders() {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private String getIdInResponse(final String body) {
        return StringUtils.substringBetween(body, "\"id\":", ",");
    }

    @Before
    public void setup() {
        testRestTemplate.withBasicAuth("admin@admin", "test");
    }

    /**
     * Test scenario when insert new institution with CNPJ with only numbers.
     */
    @Test
    public void shouldReturn200WhenInsertNewInstitutionWithCNPJWithOnlyNumbers() {
        payload = readJson("request/insertInstitutionSuccess_1.json");
        entity = new HttpEntity<String>(payload, buildHttpHeaders());
        response = testRestTemplate.exchange(BASE_ENDPOINT + INSTITUTION, HttpMethod.POST, entity, String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    /**
     * Test scenario when insert new institution.
     */
    @Test
    public void shouldReturn200WhenInsertNewInstitution() {
        payload = readJson("request/insertInstitutionSuccess_2.json");
        entity = new HttpEntity<String>(payload, buildHttpHeaders());
        response = testRestTemplate.exchange(BASE_ENDPOINT + INSTITUTION, HttpMethod.POST, entity, String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    /**
     * Test scenario when CNPJ is short.
     */
    @Test
    public void shouldReturn400WhenInsertNewInstitutionWithShortCNPJ() {
        payload = readJson("request/insertInstitutionErrorCNPJShort.json");
        entity = new HttpEntity<String>(payload, buildHttpHeaders());
        response = testRestTemplate.exchange(BASE_ENDPOINT + INSTITUTION, HttpMethod.POST, entity, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains(ErrorMessages.INVALID_CNPJ.getMessage()));
    }

    /**
     * Test scenario when CNPJ has letter.
     */
    @Test
    public void shouldReturn400WhenInsertNewInstitutionWithCNPJHasLetter() {
        payload = readJson("request/insertInstitutionErrorCNPJWithLetter.json");
        entity = new HttpEntity<String>(payload, buildHttpHeaders());
        response = testRestTemplate.exchange(BASE_ENDPOINT + INSTITUTION, HttpMethod.POST, entity, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains(ErrorMessages.INVALID_CNPJ.getMessage()));
    }

    /**
     * Test scenario when CNPJ already exist on database.
     */
    @Test
    public void shouldReturn400WhenInsertNewInstitutionWithCNPJAlreadyExistOnDatabase() {
        payload = readJson("request/insertInstitutionWithCNPJAlreadyExist.json");
        entity = new HttpEntity<String>(payload, buildHttpHeaders());
        response = testRestTemplate.exchange(BASE_ENDPOINT + INSTITUTION, HttpMethod.POST, entity, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains(ErrorMessages.CNPJ_ALREADY_EXIST.getMessage()));
    }

    /**
     * Test scenario when CNPJ is blank.
     */
    @Test
    public void shouldReturn400WhenInsertNewInstitutionWithoutCNPJ() {
        payload = readJson("request/insertInstitutionErrorWithOutCNPJ.json");
        entity = new HttpEntity<String>(payload, buildHttpHeaders());
        response = testRestTemplate.exchange(BASE_ENDPOINT + INSTITUTION, HttpMethod.POST, entity, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains(ErrorMessages.INVALID_CNPJ.getMessage()));
    }

    /**
     * Test scenario when insert new client admin user with success.
     */
    @Test
    public void shouldReturn200WhenCreateClientAdminUserWithSuccess() {
        payload = readJson("request/insertClientAdminUserSuccess.json");
        entity = new HttpEntity<String>(payload, buildHttpHeaders());
        response = testRestTemplate.exchange(BASE_ENDPOINT + USER, HttpMethod.POST, entity, String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    /**
     * Test scenario when insert new admin user with success.
     */
    @Test
    public void shouldReturn200WhenCreateAdminUserWithSuccess() {
        payload = readJson("request/insertAdminUserSuccess.json");
        entity = new HttpEntity<String>(payload, buildHttpHeaders());
        response = testRestTemplate.exchange(BASE_ENDPOINT + USER, HttpMethod.POST, entity, String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    /**
     * Test scenario when insert new client admin without CNPJ.
     */
    @Test
    public void shouldReturn400WhenCreateClientAdminWithOutCNPJ() {
        payload = readJson("request/insertClientAdminUserWithoutCNPJ.json");
        entity = new HttpEntity<String>(payload, buildHttpHeaders());
        response = testRestTemplate.exchange(BASE_ENDPOINT + USER, HttpMethod.POST, entity, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains(ErrorMessages.INVALID_CNPJ.getMessage()));
    }

    /**
     * Test scenario when insert new exam with success.
     */
    @Test
    public void shouldReturn200WhenCreateExamWithSuccess() {
        payload = readJson(INSERT_EXAM_SUCCESS);
        entity = new HttpEntity<String>(payload, buildHttpHeaders());
        response = testRestTemplate.exchange(BASE_ENDPOINT + EXAM, HttpMethod.POST, entity, String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    /**
     * Test scenario when institution does not have Pixeon balance.
     */
    @Test
    public void shouldReturn400WhenInstitutionDoesNotHavePixeonDuringExamCreation() {
        final TestRestTemplate testRestTemplate = new TestRestTemplate("other@other", "other");
        payload = readJson("request/insertExamInstituteDoesNotHavePixeon.json");
        entity = new HttpEntity<String>(payload, buildHttpHeaders());
        response = testRestTemplate.exchange(BASE_ENDPOINT + EXAM, HttpMethod.POST, entity, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains(ErrorMessages.INSTITUTE_INSUFFICIENT_PIXEON_BALANCE.getMessage()));
    }

    /**
     * Test scenario when create a exam with invalid gender.
     */
    @Test
    public void shouldReturn400WhenCreateExamWithInvalidGender() {
        payload = readJson("request/insertExamInvalidGender.json");
        entity = new HttpEntity<String>(payload, buildHttpHeaders());
        response = testRestTemplate.exchange(BASE_ENDPOINT + EXAM, HttpMethod.POST, entity, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains(ErrorMessages.INVALID_GENDER.getMessage()));
    }

    /**
     * Test scenario when user is not belong institution durant exam creation.
     */
    @Test
    public void shouldReturn400WhenUserNotBelongInstitutionDuringExamCreation() {
        final TestRestTemplate testRestTemplate = new TestRestTemplate("other@other", "other");
        payload = readJson(INSERT_EXAM_SUCCESS);
        entity = new HttpEntity<String>(payload, buildHttpHeaders());
        response = testRestTemplate.exchange(BASE_ENDPOINT + EXAM, HttpMethod.POST, entity, String.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertTrue(response.getBody().contains(ErrorMessages.ACCESS_DENIED.getMessage()));
    }

    /**
     * Test scenario when retrieve exam two times with success.
     */
    @Test
    public void shouldReturn200WhenRetrieveExamTwoTimesWithSuccess() {
        payload = readJson(INSERT_EXAM_SUCCESS);
        entity = new HttpEntity<String>(payload, buildHttpHeaders());
        response = testRestTemplate.exchange(BASE_ENDPOINT + EXAM, HttpMethod.POST, entity, String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        examId = getIdInResponse(response.getBody());
        entity = new HttpEntity<String>(buildHttpHeaders());
        response = testRestTemplate.exchange(BASE_ENDPOINT + EXAM + examId, HttpMethod.GET, entity, String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        entity = new HttpEntity<String>(buildHttpHeaders());
        response = testRestTemplate.exchange(BASE_ENDPOINT + EXAM + examId, HttpMethod.GET, entity, String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        payload = readJson(INSERT_EXAM_SUCCESS);
        entity = new HttpEntity<String>(payload, buildHttpHeaders());
        response = testRestTemplate.exchange(BASE_ENDPOINT + EXAM, HttpMethod.POST, entity, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains(ErrorMessages.INSTITUTE_INSUFFICIENT_PIXEON_BALANCE.getMessage()));
    }

    /**
     * Test scenario when delete exam with success.
     */
    @Test
    public void shouldReturn200WhenDeleteExamWithSuccess() {
        payload = readJson(INSERT_EXAM_SUCCESS);
        entity = new HttpEntity<String>(payload, buildHttpHeaders());
        response = testRestTemplate.exchange(BASE_ENDPOINT + EXAM, HttpMethod.POST, entity, String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        examId = getIdInResponse(response.getBody());
        entity = new HttpEntity<String>(buildHttpHeaders());
        response = testRestTemplate.exchange(BASE_ENDPOINT + EXAM + examId, HttpMethod.DELETE, entity, String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    /**
     * Test scenario when update exam with success.
     */
    @Test
    public void shouldReturn200WhenUpdateExamWithSuccess() {
        payload = readJson(INSERT_EXAM_SUCCESS);
        entity = new HttpEntity<String>(payload, buildHttpHeaders());
        response = testRestTemplate.exchange(BASE_ENDPOINT + EXAM, HttpMethod.POST, entity, String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        examId = getIdInResponse(response.getBody());
        payload = readJson("request/insertExamSuccess_2.json");
        entity = new HttpEntity<String>(payload, buildHttpHeaders());
        response = testRestTemplate.exchange(BASE_ENDPOINT + EXAM + examId, HttpMethod.PUT, entity, String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    /**
     * Test scenario when update exam that not exist.
     */
    @Test
    public void shouldReturn400WhenUpdateExamNotExist() {
        payload = readJson(INSERT_EXAM_SUCCESS);
        entity = new HttpEntity<String>(payload, buildHttpHeaders());
        response = testRestTemplate.exchange(BASE_ENDPOINT + EXAM + 2, HttpMethod.PUT, entity, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains(ErrorMessages.EXAM_NOT_FOUND.getMessage()));
    }

    /**
     * Test scenario when insert exam with invalid CPF.
     */
    @Test
    public void shouldReturn400WhenInsertExamWithInvalidCPF() {
        payload = readJson("request/insertExamInvalidPatientCPF.json");
        entity = new HttpEntity<String>(payload, buildHttpHeaders());
        response = testRestTemplate.exchange(BASE_ENDPOINT + EXAM, HttpMethod.POST, entity, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains(ErrorMessages.INVALID_CPF.getMessage()));
    }
}
