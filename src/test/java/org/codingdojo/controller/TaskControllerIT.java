package org.codingdojo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.codingdojo.domain.Task;
import org.codingdojo.repository.TaskRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.codingdojo.domain.TaskBuilder.aTask;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class TaskControllerIT {

    private static final String BASE_URI = "/api/task/";

    @Autowired
    private WebApplicationContext context;

    @Autowired
    //Used to delete/add the data added for tests (directly invoke the APIs interacting with the DB)
    private TaskRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @After
    public void tearDown() {
        repository.deleteAll();
    }

    @Test
    public void shouldCreateValidTask() throws Exception {
        // Given
        Task task = aTask().title("taskTitle").description("description").creationDate(now()).deadLine(now().plusMinutes(5)).build();

        // When
        ResultActions response = mockMvc.perform(post(BASE_URI)
                .content(objectMapper.writeValueAsString(task))
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        response.andExpect(status().isCreated());
    }

    @Test
    public void shouldReturn404IfTaskNotExists() throws Exception {
        // Given
        // When
        ResultActions response = mockMvc.perform(get(BASE_URI + "{id}", -1));

        // Then
        response.andExpect(status().isNotFound());
    }

    @Test
    public void shouldFindTaskById() throws Exception {
        // Given
        LocalDateTime now5 = now().plusMinutes(5);
        Task task1 = aTask().title("taskTitle1").description("description1").deadLine(now5).build();
        Task task2 = aTask().title("taskTitle2").description("description2").deadLine(now5).build();
        Task task3 = aTask().title("taskTitle3").description("description3").deadLine(now5).build();
        List<Task> tasks = repository.save(Arrays.asList(task1, task2, task3));
        assertThat(repository.count()).isEqualTo(tasks.size());

        // When
        ResultActions response = mockMvc.perform(get(BASE_URI + "{id}", task1.getId()));

        // Then
        response.andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(task1)));
    }

}