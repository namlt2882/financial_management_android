package project.baonq.service;

import android.app.Application;
import android.content.res.Resources;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import project.baonq.dao.NotificationDAO;
import project.baonq.menu.R;
import project.baonq.model.Notification;
import project.baonq.util.SyncActionImpl;

import static project.baonq.service.BaseAuthService.buildBasicConnection;
import static project.baonq.service.BaseAuthService.read;

public class NotificationService implements Runnable {
    private Application application;
    public String getNotificationUrl;
    public String getNotificationLastUpdateUrl;
    public String checkreadNotificationUrl;

    public NotificationService(Application application) {
        this.application = application;
        Resources resources = application.getBaseContext().getResources();
        getNotificationUrl = resources.getString(R.string.server_name)
                + resources.getString(R.string.get_notification_url);
        getNotificationLastUpdateUrl = resources.getString(R.string.server_name)
                + resources.getString(R.string.get_notification_lastUpdate_url);
        checkreadNotificationUrl = resources.getString(R.string.server_name)
                + resources.getString(R.string.check_read_notification_url);
    }

    public List<Notification> addNotification(List<Notification> notifications) {
        NotificationDAO dao = new NotificationDAO(application);
        return dao.insertOrUpdate(notifications);
    }

    public Long getServerLastUpdate() throws Exception {
        Long result;
        URL url = new URL(getNotificationLastUpdateUrl);
        HttpURLConnection conn = buildBasicConnection(url, true);
        BufferedReader in = null;
        ObjectMapper om = new ObjectMapper();
        try {
            //read response value
            if (conn.getResponseCode() == 200) {
                in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String tmp = read(in);
                result = om.readValue(tmp, Long.class);
            } else {
                in = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                throw new Exception(read(in));

            }
        } finally {
            if (in != null) {
                in.close();
            }
        }
        return result;
    }

    public void checkNotificationRead(List<Long> ids) {
        NotificationDAO notificationDAO = new NotificationDAO(application);
        List<Notification> notifications = notificationDAO.findByIds(ids);
        long currentTime = System.currentTimeMillis();
        notifications.forEach(notification -> {
            notification.setIs_readed(true);
            notification.setLast_update(currentTime);
        });
        notificationDAO.insertOrUpdate(notifications);
    }

    public void checkAllNotificationRead() {
        List<Long> unreadNotificationIds = getUnreadNotifications().stream()
                .map(notification -> notification.getId()).collect(Collectors.toList());
        checkNotificationRead(unreadNotificationIds);
    }

    public List<Notification> getUnreadNotifications() {
        return new NotificationDAO(application).findUnreadNotifications();
    }

    public List<Notification> findAll() {
        return new NotificationDAO(application).findAll();
    }

    public Long getLastUpdateTime() {
        Long result = new NotificationDAO(application).findLastUpdateTime();
        if (result == null) {
            result = Long.valueOf(0);
        }
        return result;
    }

    public List<Notification> getUpdatableRecords(Long lastUpdate) {
        return new NotificationDAO(application).findByLastUpdate(lastUpdate);
    }

    public void syncWithLocal(List<Notification> syncData) {
        NotificationDAO dao = new NotificationDAO(application);
        //get server id
        List<Long> serverIds = syncData.stream()
                .filter(notification -> notification.getServer_id() != null)
                .map(notification -> notification.getServer_id()).collect(Collectors.toList());
        //get local data
        List<Notification> localData = dao.findByServerIds(serverIds);
        Map<Long, Notification> map = new HashMap<>();
        //add sync data to map
        syncData.forEach(notification -> map.put(notification.getServer_id(), notification));
        //add local id to sync data
        localData.forEach(originalNotification -> {
            Notification tmp = map.get(originalNotification.getServer_id());
            tmp.setId(originalNotification.getId());
        });
    }

    @Override
    public void run() {
        while (true) {
            FetchNotificationAction fetchAction = new FetchNotificationAction(this);
            CheckReadNotificationAction checkReadAction = new CheckReadNotificationAction(this);
            try {
                fetchAction.doAction();
                checkReadAction.doAction();
                Thread.sleep(5000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void insertOrUpdate(List<Notification> notifications) {
        new NotificationDAO(application).insertOrUpdate(notifications);
    }

    public static class FetchNotificationAction extends SyncActionImpl {
        NotificationService notificationService;

        public FetchNotificationAction(NotificationService notificationService) {
            this.notificationService = notificationService;
        }

        @Override
        public void beforeSynchronize() {
        }

        @Override
        public void afterSynchronize() {
            Notification[] syncData = (Notification[]) getSyncData();
            List<Notification> syncDataList = Arrays.asList(syncData);
            notificationService.syncWithLocal(syncDataList);
            notificationService.insertOrUpdate(syncDataList);
        }

        @Override
        public Object synchronize() {
            System.out.println("SENDING REQUEST TO URL:" + notificationService.getNotificationUrl + ", method:GET");
            Long lastUpdate = notificationService.getLastUpdateTime();
            Notification[] result = new Notification[]{};
            URL url = null;
            try {
                url = new URL(notificationService.getNotificationUrl + "?lastUpdate=" + lastUpdate);
                HttpURLConnection conn = buildBasicConnection(url, true);
                BufferedReader in = null;
                ObjectMapper om = new ObjectMapper();
                try {
                    //read response value
                    if (conn.getResponseCode() == 200) {
                        in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        String tmp = read(in);
                        result = om.readValue(tmp, new Notification[]{}.getClass());
                    } else {
                        in = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                        throw new Exception(read(in));

                    }
                } finally {
                    if (in != null) {
                        in.close();
                    }
                }
                return result;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static class CheckReadNotificationAction extends SyncActionImpl {

        private NotificationService notificationService;

        public CheckReadNotificationAction(NotificationService notificationService) {
            this.notificationService = notificationService;
        }

        @Override
        public void beforeSynchronize() {

        }

        @Override
        public void afterSynchronize() {

        }

        @Override
        public Object synchronize() {
            System.out.println("SENDING REQUEST TO URL:" + notificationService.checkreadNotificationUrl + ", method:POST");
            try {
                Long lastUpdate = notificationService.getServerLastUpdate();
                List<Notification> updatableRecords = notificationService.getUpdatableRecords(lastUpdate);
                if (updatableRecords != null && !updatableRecords.isEmpty()) {
                    URL url = new URL(notificationService.checkreadNotificationUrl);
                    HttpURLConnection conn = buildBasicConnection(url, true);
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

                    conn.setDoOutput(true);
                    BufferedReader in = null;
                    ObjectMapper om = new ObjectMapper();
                    try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
                         BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(wr, "UTF-8"));) {
                        //write data to request
                        String entity = om.writeValueAsString(updatableRecords);
                        writer.write(entity);
                        //read response value
                        if (conn.getResponseCode() != 200) {
                            in = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                            throw new Exception(read(in));
                        }
                    } finally {
                        if (in != null) {
                            in.close();
                        }
                    }
                }
                return null;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
