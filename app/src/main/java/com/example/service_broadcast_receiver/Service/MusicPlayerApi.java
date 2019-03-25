package com.example.service_broadcast_receiver.Service;

import java.util.List;

public interface MusicPlayerApi {
    String playNextItem();
    List<String> getHistory();
}
