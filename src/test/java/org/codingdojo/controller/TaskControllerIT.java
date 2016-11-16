package org.codingdojo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.icegreen.greenmail.junit.GreenMailRule;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetupTest;
import org.apache.commons.lang3.StringUtils;
import org.codingdojo.domain.Task;
import org.codingdojo.domain.User;
import org.codingdojo.repository.TaskRepository;
import org.codingdojo.repository.UserRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
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

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.time.LocalDateTime.now;
import static javax.mail.internet.MimeMessage.RecipientType.TO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.codingdojo.domain.TaskBuilder.aTask;
import static org.codingdojo.domain.UserBuilder.anUser;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class TaskControllerIT {

    private static final String BASE_URI = "/api/task/";

    @Rule
    public final GreenMailRule greenMail = new GreenMailRule(ServerSetupTest.SMTP);

    @Autowired
    private WebApplicationContext context;

    @Autowired
    //Used to delete/add the data added for tests (directly invoke the APIs interacting with the DB)
    private TaskRepository repository;

    @Autowired
    //Used to delete/add the data added for tests (directly invoke the APIs interacting with the DB)
    private UserRepository userRepository;

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
        userRepository.deleteAll();
    }

    @Test
    public void shouldCreateValidTask() throws Exception {
        // Given
        Task task = aTask().title("taskTitle").description("description").creationDate(now()).deadLine(now()).build();

        // When
        ResultActions response = mockMvc.perform(post(BASE_URI)
                .content(objectMapper.writeValueAsString(task))
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        response.andExpect(status().isCreated());
        assertThat(repository.count()).isEqualTo(1);
    }

    @Test
    public void shouldReturn400WhenCreateInvalidTaskNoTitle() throws Exception {
        // Given
        Task task = aTask().build();

        // When
        ResultActions response = mockMvc.perform(post(BASE_URI)
                .content(objectMapper.writeValueAsString(task))
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        response.andExpect(status().isBadRequest());
        assertThat(repository.count()).isEqualTo(0);
    }

    @Test
    public void shouldFindTaskById() throws Exception {
        // Given
        Task task1 = aTask().title("taskTitle1").description("description1").deadLine(now()).build();
        Task task2 = aTask().title("taskTitle2").description("description2").deadLine(now()).build();
        Task task3 = aTask().title("taskTitle3").description("description3").deadLine(now()).build();
        List<Task> tasks = repository.save(Arrays.asList(task1, task2, task3));
        assertThat(repository.count()).isEqualTo(tasks.size());

        // When
        ResultActions response = mockMvc.perform(get(BASE_URI + "{id}", task1.getId()));

        // Then
        response.andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(task1)));
    }

    @Test
    public void shouldReturn404WhenFindUnknownTask() throws Exception {
        // Given
        // When
        ResultActions response = mockMvc.perform(get(BASE_URI + "{id}", -1));

        // Then
        response.andExpect(status().isNotFound());
    }

    @Test
    public void shouldFindAllTasks() throws Exception {
        // Given
        Task task1 = aTask().title("taskTitle1").description("description1").deadLine(now()).build();
        Task task2 = aTask().title("taskTitle2").description("description2").deadLine(now()).build();
        Task task3 = aTask().title("taskTitle3").description("description3").deadLine(now()).build();
        List<Task> tasks = repository.save(Arrays.asList(task1, task2, task3));
        assertThat(repository.count()).isEqualTo(tasks.size());

        // When
        ResultActions response = mockMvc.perform(get(BASE_URI));

        // Then
        response.andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(tasks)));
    }

    @Test
    public void shouldFindTaskByTitle() throws Exception {
        // Given
        Task task1 = aTask().title("taskTitle1").description("description1").deadLine(now()).build();
        Task task2 = aTask().title("taskTitle2").description("description2").deadLine(now()).build();
        Task task3 = aTask().title("taskTitle3").description("description3").deadLine(now()).build();
        List<Task> tasks = repository.save(Arrays.asList(task1, task2, task3));
        assertThat(repository.count()).isEqualTo(tasks.size());

        // When
        ResultActions response = mockMvc.perform(get(BASE_URI + "/title/{title}", task1.getTitle()));

        // Then
        response.andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(Collections.singletonList(task1))));
    }

    @Test
    public void shouldFindAllTasksWithSameTitle() throws Exception {
        // Given
        Task task1 = aTask().title("taskTitle").description("description1").deadLine(now()).build();
        Task task2 = aTask().title("taskTitle").description("description2").deadLine(now()).build();
        Task task3 = aTask().title("taskTitle3").description("description3").deadLine(now()).build();
        List<Task> tasks = repository.save(Arrays.asList(task1, task2, task3));
        assertThat(repository.count()).isEqualTo(tasks.size());

        // When
        ResultActions response = mockMvc.perform(get(BASE_URI + "/title/{title}", task1.getTitle()));

        // Then
        response.andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(Arrays.asList(task1, task2))));
    }

    @Test
    public void shouldDeleteTask() throws Exception {
        // Given
        Task task1 = aTask().title("taskTitle1").description("description1").deadLine(now()).build();
        Task task2 = aTask().title("taskTitle2").description("description2").deadLine(now()).build();
        Task task3 = aTask().title("taskTitle3").description("description3").deadLine(now()).build();
        List<Task> tasks = repository.save(Arrays.asList(task1, task2, task3));
        assertThat(repository.count()).isEqualTo(tasks.size());

        // When
        ResultActions response = mockMvc.perform(delete(BASE_URI + "/{id}", task1.getId()));

        // Then
        response.andExpect(status().isNoContent())
                .andExpect(content().string(StringUtils.EMPTY));
        assertThat(repository.count()).isEqualTo(tasks.size() - 1);
    }

    @Test
    public void shouldReturn400WhenDeleteUnknownTask() throws Exception {
        // Given
        // When
        ResultActions response = mockMvc.perform(delete(BASE_URI + "/{id}", -1L));

        // Then
        response.andExpect(status().isNotFound());
    }

    @Test
    public void shouldAssignTaskToUserAndNotifyHim() throws Exception {
        // Given
        Task task = repository.save(aTask().title("taskTitle1").description("description1").deadLine(now().plusMinutes(5)).build());
        assertThat(repository.exists(task.getId()));

        User user = userRepository.save(anUser().name("userName1").email("user.email1@test.org").build());
        assertThat(userRepository.exists(user.getId()));

        // When
        ResultActions response = mockMvc.perform(put(BASE_URI + "{taskId}/user/{userId}", task.getId(), user.getId()));

        // Then
        task.setUser(user);
        response.andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(task)));

        assertThat(greenMail.getReceivedMessages()).hasSize(1);
        MimeMessage mail = greenMail.getReceivedMessages()[0];
        assertThat(mail.getSubject()).isEqualTo("Task notification");
        assertThat(mail.getRecipients(TO)[0]).isEqualTo(new InternetAddress(user.getEmail()));
        assertThat(GreenMailUtil.getBody(mail)).isEqualTo(String.format("The task: '%s' has been assigned to you.", task.getTitle()));
    }

    @Test
    public void shouldAssignTaskFromPreviousUserToNewUserAndNotifyThem() throws Exception {
        // Given
        User previousUser = anUser().name("userName1").email("user.email1@test.org").build();
        User user = anUser().name("userName2").email("user.email2@test.org").build();
        List<User> users = userRepository.save(Arrays.asList(previousUser, user));
        assertThat(userRepository.count()).isEqualTo(users.size());

        Task task = repository.save(aTask().title("taskTitle1").description("description1").deadLine(now().plusMinutes(5)).user(previousUser).build());
        assertThat(repository.exists(task.getId()));

        // When
        ResultActions response = mockMvc.perform(put(BASE_URI + "{taskId}/user/{userId}", task.getId(), user.getId()));

        // Then
        task.setUser(user);
        response.andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(task)));

        assertThat(greenMail.getReceivedMessages()).hasSize(2);

        MimeMessage mailToUser = greenMail.getReceivedMessages()[0];
        assertThat(mailToUser.getSubject()).isEqualTo("Task notification");
        assertThat(mailToUser.getRecipients(TO)[0]).isEqualTo(new InternetAddress(user.getEmail()));
        assertThat(GreenMailUtil.getBody(mailToUser)).isEqualTo(String.format("The task: '%s' has been assigned to you.", task.getTitle()));

        MimeMessage mailToPreviousUser = greenMail.getReceivedMessages()[1];
        assertThat(mailToPreviousUser.getSubject()).isEqualTo("Task notification");
        assertThat(mailToPreviousUser.getRecipients(TO)[0]).isEqualTo(new InternetAddress(previousUser.getEmail()));
        assertThat(GreenMailUtil.getBody(mailToPreviousUser)).isEqualTo(String.format("The task: '%s' has been assigned to somebody else.", task.getTitle()));

        assertThat(repository.findOne(task.getId()).getUser().getId()).isEqualTo(user.getId());
    }

    @Test
    public void shouldReturn400WhenAssignOverdueTaskToUser() throws Exception {
        // Given
        Task task = repository.save(aTask().title("taskTitle1").description("description1").deadLine(now().minusMinutes(5)).build());
        assertThat(repository.exists(task.getId()));

        User user = userRepository.save(anUser().name("userName1").email("user.email1@test.org").build());
        assertThat(userRepository.exists(user.getId()));

        // When
        ResultActions response = mockMvc.perform(put(BASE_URI + "{taskId}/user/{userId}", task.getId(), user.getId()));

        // Then
        response.andExpect(status().isBadRequest());
        assertThat(greenMail.getReceivedMessages()).hasSize(0);
        assertThat(repository.findOne(task.getId()).getUser()).isNull();
    }

    @Test
    public void shouldReturn400WhenAssignDoneTaskToUser() throws Exception {
        // Given
        Task task = repository.save(aTask().title("taskTitle1").description("description1").done(true).build());
        assertThat(repository.exists(task.getId()));

        User user = userRepository.save(anUser().name("userName1").email("user.email1@test.org").build());
        assertThat(userRepository.exists(user.getId()));

        // When
        ResultActions response = mockMvc.perform(put(BASE_URI + "{taskId}/user/{userId}", task.getId(), user.getId()));

        // Then
        response.andExpect(status().isBadRequest());
        assertThat(greenMail.getReceivedMessages()).hasSize(0);
        assertThat(repository.findOne(task.getId()).getUser()).isEqualTo(null);
    }
}