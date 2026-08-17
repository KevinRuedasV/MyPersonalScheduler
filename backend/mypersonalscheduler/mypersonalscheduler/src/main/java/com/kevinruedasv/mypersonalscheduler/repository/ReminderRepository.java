package com.kevinruedasv.mypersonalscheduler.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.kevinruedasv.mypersonalscheduler.model.Reminder;
import com.kevinruedasv.mypersonalscheduler.model.ReminderStatus;

public interface ReminderRepository
        extends MongoRepository<Reminder, String> {

    List<Reminder> findByUserIdOrderByReminderDateTimeAsc(
            String userId
    );

    List<Reminder> findByUserIdAndStatusOrderByReminderDateTimeAsc(
            String userId,
            ReminderStatus status
    );

    List<Reminder> findByStatusAndReminderDateTimeLessThanEqual(
            ReminderStatus status,
            LocalDateTime dateTime
    );

    void deleteByUserId(String userId);
}