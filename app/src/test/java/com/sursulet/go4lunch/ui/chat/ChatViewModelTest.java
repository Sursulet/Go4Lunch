package com.sursulet.go4lunch.ui.chat;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.sursulet.go4lunch.model.Message;
import com.sursulet.go4lunch.model.User;
import com.sursulet.go4lunch.repository.ChatRepository;
import com.sursulet.go4lunch.repository.UserRepository;
import com.sursulet.go4lunch.ui.detail.DetailPlaceUiModel;
import com.sursulet.go4lunch.utils.LiveDataTestUtils;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class ChatViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    ChatRepository chatRepository;

    @Mock
    UserRepository userRepository;

    MutableLiveData<List<Message>> allMessagesLiveData;

    ChatViewModel viewModel;

    @Before
    public void setUp() {
        allMessagesLiveData = new MutableLiveData<>();

        Mockito.doReturn(allMessagesLiveData).when(chatRepository).getAllMessages(Mockito.any());

        viewModel = new ChatViewModel(chatRepository, userRepository);
    }

    @Test
    public void basicDisplay() throws InterruptedException {
        //Given
        allMessagesLiveData.setValue(getMessages());

        List<MessageUiModel> result = LiveDataTestUtils.getOrAwaitValue(viewModel.getUiModelMutableLiveData());

        assertEquals(2, result.size());
    }

    private List<Message> getMessages() {
        List<Message> messages = new ArrayList<>();
        Message message = new Message("Hello", new User("0", "Peach", "https://unsplash.com/photos/gKXKBY-C-Dk"));
        messages.add(message);
        return messages;
    }

}