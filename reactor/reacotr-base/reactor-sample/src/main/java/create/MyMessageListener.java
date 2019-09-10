package create;

import java.util.List;

interface MyMessageListener<T> {
    void onMessage(List<T> chunk);
}