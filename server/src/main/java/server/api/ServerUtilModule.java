package server.api;

import commons.Event;
import org.springframework.stereotype.Component;
import server.database.EventRepository;

import java.sql.Timestamp;
import java.util.Date;
@Component
public class ServerUtilModule implements GerneralServerUtil{

    /**
     * adw
     * @param eventRepo dwa
     * @param inviteCode dwa
     */
    public void updateDate(EventRepository eventRepo, long inviteCode) {
        Event event = eventRepo.findById(inviteCode).get();
        Date date = new Date();
        Timestamp timestamp2 = new Timestamp(date.getTime());
        event.setLastActivity(timestamp2);
        eventRepo.save(event);
    }
}
