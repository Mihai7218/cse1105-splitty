package server.api;

import server.database.EventRepository;

public interface GerneralServerUtil {

    /**
     * dwa
     * @param eventRepo dwa
     * @param inviteCode dwa
     */
    void updateDate(EventRepository eventRepo, long inviteCode);
}
