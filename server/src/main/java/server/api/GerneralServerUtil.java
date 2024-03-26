package server.api;

import org.springframework.stereotype.Service;
import server.database.EventRepository;

@Service
public interface GerneralServerUtil {

    /**
     * dwa
     * @param eventRepo dwa
     * @param inviteCode dwa
     */
    void updateDate(EventRepository eventRepo, long inviteCode);
}
