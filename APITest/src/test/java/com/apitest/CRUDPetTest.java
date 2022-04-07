package com.apitest;

import com.apitest.config.AppConfig;
import com.apitest.dataModel.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = AppConfig.class)
public class CRUDPetTest extends BasePetStoreTest {

    @Test
    @DisplayName("Create one pet")
    public void createOnePet() {
        Pet newPet = prepareBasicPet();
        ResponseEntity<Pet> response = restService.postForObject(endpoints.getBaseUrl(), headers, newPet, Pet.class, endpoints.getPetEndpoint("basicEndpoint"));
        assertThat("There is wrong response status! ", response.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat("Response doesn't contain created pet", response.getBody(), equalTo(newPet));
    }

    @Test
    @DisplayName("Create one pet with multiple tags")
    public void createOnePetWithMultipleTags() {
        Pet newPet = prepareBasicPet();
        newPet.setTags(generateBasicTags(3));
        ResponseEntity<Pet> response = restService.postForObject(endpoints.getBaseUrl(), headers, newPet, Pet.class, endpoints.getPetEndpoint("basicEndpoint"));
        assertThat("There is wrong response status! ", response.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat("Response doesn't contain created pet", response.getBody(), equalTo(newPet));
    }

    @Test
    @DisplayName("Create one pet without tags")
    public void createOnePetWithoutTags() {
        Pet newPet = prepareBasicPet();
        newPet.setTags(Collections.EMPTY_LIST);
        ResponseEntity<Pet> response = restService.postForObject(endpoints.getBaseUrl(), headers, newPet, Pet.class, endpoints.getPetEndpoint("basicEndpoint"));
        assertThat("There is wrong response status! ", response.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat("Response doesn't contain created pet", response.getBody(), equalTo(newPet));
    }


    @Test
    @DisplayName("Create one pet with unavailable status")
    public void createOnePetWithUnavailableStatus() {
        Pet newPet = prepareBasicPet();
        newPet.setStatus(Pet.PetStatuses.UNDEFINED.getStatus());
        ResponseEntity<Pet> response = restService.postForObject(endpoints.getBaseUrl(), headers, newPet, Pet.class, endpoints.getPetEndpoint("basicEndpoint"));
        assertThat("There is wrong response status! ", response.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
    }

    @Test
    @DisplayName("Update existing pet")
    public void updateExistingPet() {
        Pet newPet = prepareBasicPet();
        restService.postForObject(endpoints.getBaseUrl(), headers, newPet, Pet.class, endpoints.getPetEndpoint("basicEndpoint"));
        newPet.setStatus(Pet.PetStatuses.SOLD.getStatus());
        ResponseEntity<Pet> response = restService.putForObject(endpoints.getBaseUrl(), headers, newPet, Pet.class, endpoints.getPetEndpoint("basicEndpoint"));
        assertThat("There is wrong response status! ", response.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat("Response has incorrect body", response.getBody(), equalTo(newPet));
    }

    @Test
    @DisplayName("Update not existing pet")
    public void updateNotExistingPet() {
        Pet newPet = prepareBasicPet();
        ResponseEntity<String> response = restService.putForObject(endpoints.getBaseUrl(), headers, newPet, String.class, endpoints.getPetEndpoint("basicEndpoint"));
        assertThat("There is wrong response status! ", response.getStatusCode(), equalTo(HttpStatus.NOT_FOUND));
        assertThat("Response doesn't contain created pet", response.getBody(), equalTo("Pet not found"));
    }

    @Test
    @DisplayName("Find pet by status")
    public void findPetByStatus() {
        Pet newPet = prepareBasicPet();
        restService.postForObject(endpoints.getBaseUrl(), headers, newPet, String.class, endpoints.getPetEndpoint("basicEndpoint"));
        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("status", Pet.PetStatuses.AVAILABLE.getStatus());
        ResponseEntity<Pet[]> response = restService.getForObject(endpoints.getBaseUrl(), headers, queryParam, Pet[].class, endpoints.getPetEndpoint("findByStatus"));
        assertThat("There is wrong response status! ", response.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat("Response doesn't contain created pet", Arrays.asList(Objects.requireNonNull(response.getBody())), hasItem(newPet));
    }

    @Test
    @DisplayName("Find pet without status")
    public void findPetByDefaultStatus() {
        Pet newPet = prepareBasicPet();
        restService.postForObject(endpoints.getBaseUrl(), headers, newPet, String.class, endpoints.getPetEndpoint("basicEndpoint"));
        ResponseEntity<String> response = restService.getForObject(endpoints.getBaseUrl(), headers, String.class, endpoints.getPetEndpoint("findByStatus"));
        assertThat("There is wrong response status! ", response.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
        assertThat("Response message is not as expected", response.getBody(), equalTo("No status provided. Try again?"));
    }

    @Test
    @DisplayName("Find pet by invalid status")
    public void findPetByInvalidStatus() {
        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("status", Pet.PetStatuses.UNDEFINED.getStatus());
        ResponseEntity<StoreResponse> response = restService.getForObject(endpoints.getBaseUrl(), headers, queryParam, StoreResponse.class, endpoints.getPetEndpoint("findByStatus"));
        assertThat("There is wrong response status! ", response.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
        assertThat("There is wrong response status! ", response.getBody().getCode(), equalTo(400));
        assertThat("Response message is not as expected", response.getBody().getMessage(), equalTo("Input error: query parameter `status value `undefined` is not in the allowable values `[available, pending, sold]`"));
    }

    @Test
    @DisplayName("Find pet by tag")
    public void findPetByTag() {
        Pet newPet = prepareBasicPet();
        restService.postForObject(endpoints.getBaseUrl(), headers, newPet, String.class, endpoints.getPetEndpoint("basicEndpoint"));
        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.addAll("tags", newPet.getTags().stream().map(Tag::getName).collect(Collectors.toList()));
        ResponseEntity<Pet[]> response = restService.getForObject(endpoints.getBaseUrl(), headers, queryParam, Pet[].class, endpoints.getPetEndpoint("findByTags"));
        assertThat("There is wrong response status! ", response.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat("Response doesn't contain created pet", Arrays.asList(Objects.requireNonNull(response.getBody())), hasItem(newPet));
    }

    @Test
    @DisplayName("Find pet by tags")
    public void findPetByTags() {
        Pet newPet = prepareBasicPet();
        newPet.setTags(generateBasicTags(3));
        restService.postForObject(endpoints.getBaseUrl(), headers, newPet, String.class, endpoints.getPetEndpoint("basicEndpoint"));
        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.addAll("tags", newPet.getTags().stream().map(Tag::getName).collect(Collectors.toList()));
        ResponseEntity<Pet[]> response = restService.getForObject(endpoints.getBaseUrl(), headers, queryParam, Pet[].class, endpoints.getPetEndpoint("findByTags"));
        assertThat("There is wrong response status! ", response.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat("Response doesn't contain created pet", Arrays.asList(Objects.requireNonNull(response.getBody())), hasItem(newPet));
    }

    @Test
    @DisplayName("Find pets by tags")
    public void findPetsByTags() {
        Pet newPet = prepareBasicPet();
        newPet.setTags(generateBasicTags(2));
        restService.postForObject(endpoints.getBaseUrl(), headers, newPet, String.class, endpoints.getPetEndpoint("basicEndpoint"));
        Pet newPet2 = prepareBasicPet();
        newPet2.setTags(generateBasicTags(2));
        restService.postForObject(endpoints.getBaseUrl(), headers, newPet2, String.class, endpoints.getPetEndpoint("basicEndpoint"));
        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.addAll("tags", newPet.getTags().stream().map(Tag::getName).collect(Collectors.toList()));
        queryParam.addAll("tags", newPet2.getTags().stream().map(Tag::getName).collect(Collectors.toList()));
        ResponseEntity<Pet[]> response = restService.getForObject(endpoints.getBaseUrl(), headers, queryParam, Pet[].class, endpoints.getPetEndpoint("findByTags"));
        assertThat("There is wrong response status! ", response.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat("Response doesn't contain created pets", Arrays.asList(Objects.requireNonNull(response.getBody())), hasItems(newPet, newPet2));
        assertThat("Response size is not as expected", Arrays.asList(Objects.requireNonNull(response.getBody())), hasSize(2));
    }

    @Test
    @DisplayName("Find pet by id")
    public void findPetById() {
        Pet newPet = prepareBasicPet();
        restService.postForObject(endpoints.getBaseUrl(), headers, newPet, String.class, endpoints.getPetEndpoint("basicEndpoint"));
        Map<String,Long> urlParams = new HashMap<>();
        urlParams.put("pet_id", newPet.getId());
        ResponseEntity<Pet> response = restService.getForObject(endpoints.getBaseUrl(), headers, urlParams, Pet.class, endpoints.getPetEndpoint("petId"));
        assertThat("There is wrong response status! ", response.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat("Response doesn't contain created pet", response.getBody(), equalTo(newPet));
    }

    @Test
    @DisplayName("Try to find not exist pet by id")
    public void notExistPetById() {
        Pet newPet = prepareBasicPet();
        Map<String,Long> urlParams = new HashMap<>();
        urlParams.put("pet_id", newPet.getId());
        ResponseEntity<String> response = restService.getForObject(endpoints.getBaseUrl(), headers, urlParams, String.class, endpoints.getPetEndpoint("petId"));
        assertThat("There is wrong response status! ", response.getStatusCode(), equalTo(HttpStatus.NOT_FOUND));
        assertThat("Response message is not as expected", response.getBody(), equalTo("Pet not found"));
    }

    @Test
    @DisplayName("Try to find pet by invalid id")
    public void findPetByInvalidId() {
        Map<String,Object> urlParams = new HashMap<>();
        urlParams.put("pet_id", RandomStringUtils.randomAlphanumeric(10));
        ResponseEntity<StoreResponse> response = restService.getForObject(endpoints.getBaseUrl(), headers, urlParams, StoreResponse.class, endpoints.getPetEndpoint("petId"));
        assertThat("There is wrong response status! ", response.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
        assertThat("There is wrong response status! ", response.getBody().getCode(), equalTo(400));
        assertThat("Response message is not as expected", response.getBody().getMessage(), equalTo("Invalid ID supplied"));
    }

    @Test
    @DisplayName("Update pet by form")
    public void updatePetByForm() {
        Pet newPet = prepareBasicPet();
        restService.postForObject(endpoints.getBaseUrl(), headers, newPet, String.class, endpoints.getPetEndpoint("basicEndpoint"));
        newPet.setStatus(Pet.PetStatuses.SOLD.getStatus());
        newPet.setName("new_name");
        Map<String,Object> urlParams = new HashMap<>();
        urlParams.put("pet_id", newPet.getId());
        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("name", newPet.getName());
        queryParam.add("status", newPet.getStatus());
        ResponseEntity<Pet> response = restService.postForObject(endpoints.getBaseUrl(), headers, urlParams, queryParam, Pet.class, endpoints.getPetEndpoint("petId"));
        assertThat("There is wrong response status! ", response.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat("Response doesn't contain changed pet", response.getBody(), equalTo(newPet));
    }

    @Test
    @DisplayName("Update pet by form without name")
    public void updatePetByFormWithoutName() {
        Pet newPet = prepareBasicPet();
        restService.postForObject(endpoints.getBaseUrl(), headers, newPet, String.class, endpoints.getPetEndpoint("basicEndpoint"));
        Map<String,Object> urlParams = new HashMap<>();
        urlParams.put("pet_id", newPet.getId());
        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("status", newPet.getStatus());
        ResponseEntity<String> response = restService.postForObject(endpoints.getBaseUrl(), headers, urlParams, queryParam, String.class, endpoints.getPetEndpoint("petId"));
        assertThat("There is wrong response status! ", response.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
        assertThat("Response message is not as expected", response.getBody(), equalTo("No Name provided. Try again?"));
    }

    @Test
    @DisplayName("Update pet by form witt emptyId")
    public void updatePetByFormWithEmptyId() {
        Pet newPet = prepareBasicPet();
        restService.postForObject(endpoints.getBaseUrl(), headers, newPet, String.class, endpoints.getPetEndpoint("basicEndpoint"));
        Map<String,Object> urlParams = new HashMap<>();
        urlParams.put("pet_id", "");
        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("name", newPet.getName());
        queryParam.add("status", newPet.getStatus());
        ResponseEntity<String> response = restService.postForObject(endpoints.getBaseUrl(), headers, urlParams, queryParam, String.class, endpoints.getPetEndpoint("petId"));
        assertThat("There is wrong response status! ", response.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
        assertThat("Response message is not as expected", response.getBody(), equalTo("No Pet provided. Try again?"));
    }

    @Test
    @DisplayName("Update not exist pet by form witt emptyId")
    public void updateNotExistPetByForm() {
        Pet newPet = prepareBasicPet();
        Map<String,Object> urlParams = new HashMap<>();
        urlParams.put("pet_id", newPet.getId());
        MultiValueMap<String, String> queryParam = new LinkedMultiValueMap<>();
        queryParam.add("name", newPet.getName());
        queryParam.add("status", newPet.getStatus());
        ResponseEntity<String> response = restService.postForObject(endpoints.getBaseUrl(), headers, urlParams, queryParam, String.class, endpoints.getPetEndpoint("petId"));
        assertThat("There is wrong response status! ", response.getStatusCode(), equalTo(HttpStatus.NOT_FOUND));
        assertThat("Response message is not as expected", response.getBody(), equalTo("Pet not found"));
    }

    @Test
    @DisplayName("Delete pet by id")
    public void deletePetById() {
        Pet newPet = prepareBasicPet();
        restService.postForObject(endpoints.getBaseUrl(), headers, newPet, String.class, endpoints.getPetEndpoint("basicEndpoint"));
        Map<String,Long> urlParams = new HashMap<>();
        urlParams.put("pet_id", newPet.getId());
        ResponseEntity<String> response = restService.deleteForObject(endpoints.getBaseUrl(), headers, urlParams, String.class, endpoints.getPetEndpoint("petId"));
        assertThat("There is wrong response status! ", response.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat("Response doesn't contain created pet", response.getBody(), equalTo("Pet deleted"));
        ResponseEntity<String> getResponse = restService.getForObject(endpoints.getBaseUrl(), headers, urlParams, String.class, endpoints.getPetEndpoint("petId"));
        assertThat("There is wrong response status! ", getResponse.getStatusCode(), equalTo(HttpStatus.NOT_FOUND));
        assertThat("Response message is not as expected", getResponse.getBody(), equalTo("Pet not found"));
    }

    @Test
    @DisplayName("Delete pet without id")
    public void deletePetWithoutId() {
        Pet newPet = prepareBasicPet();
        Map<String,Object> urlParams = new HashMap<>();
        urlParams.put("pet_id", "");
        ResponseEntity<String> response = restService.deleteForObject(endpoints.getBaseUrl(), headers, urlParams, String.class, endpoints.getPetEndpoint("petId"));
        assertThat("There is wrong response status! ", response.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
        assertThat("Response message is not as expected", response.getBody(), equalTo("No petId provided. Try again?"));
    }

    @Test
    @DisplayName("Upload image for pet by id")
    public void uploadImageForPetById() throws IOException {
        Pet newPet = prepareBasicPet();
        restService.postForObject(endpoints.getBaseUrl(), headers, newPet, String.class, endpoints.getPetEndpoint("basicEndpoint"));
        Map<String,Long> urlParams = new HashMap<>();
        urlParams.put("pet_id", newPet.getId());
        File image = resourceLoader.getResource(
                "classpath:test_image.png").getFile();
        ResponseEntity<Pet> response = restService.postImageForObject(endpoints.getBaseUrl(), image, urlParams, Pet.class, endpoints.getPetEndpoint("uploadImage"));
        assertThat("There is wrong response status! ", response.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat("Response message contains old value", response.getBody().getPhotoUrls(), not(equalTo(newPet.getPhotoUrls())));
    }

    @Test
    @DisplayName("Upload image for not existing pet by id")
    public void uploadImageForNotExistingPetById() throws IOException {
        Pet newPet = prepareBasicPet();
        Map<String,Long> urlParams = new HashMap<>();
        urlParams.put("pet_id", newPet.getId());
        File image = resourceLoader.getResource(
                "classpath:test_image.png").getFile();
        ResponseEntity<String> response = restService.postImageForObject(endpoints.getBaseUrl(), image, urlParams, String.class, endpoints.getPetEndpoint("uploadImage"));
        assertThat("There is wrong response status! ", response.getStatusCode(), equalTo(HttpStatus.NOT_FOUND));
        assertThat("Response doesn't contain created pet", response.getBody(), equalTo("Pet not found"));
    }

    private List<Tag> generateBasicTags(int number) {
        return IntStream.range(0, number).mapToObj(i -> generateBasicTag()).collect(Collectors.toList());
    }

    private Tag generateBasicTag() {
        Random rnd = new Random();
        return Tag.builder().id(rnd.nextLong()).name(randomAlphabetic(10))
                .build();
    }

    private Category generateBasicCategory() {
        Random rnd = new Random();
        return Category.builder().id(rnd.nextLong())
                .name(randomAlphabetic(10))
                .build();
    }

    private Pet prepareBasicPet() {
        Random rnd = new Random();
        return Pet.builder().id(rnd.nextLong()).name(randomAlphabetic(10))
                .status(Pet.PetStatuses.AVAILABLE.getStatus())
                .category(generateBasicCategory())
                .tags(Arrays.asList(generateBasicTag()))
                .photoUrls(Arrays.asList(randomAlphanumeric(10)))
                .build();
    }
}