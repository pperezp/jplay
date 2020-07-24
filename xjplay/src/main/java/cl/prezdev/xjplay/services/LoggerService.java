package cl.prezdev.xjplay.services;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggerService {
    private Logger logger;
    private static LoggerService loggerService;

    public static <T> LoggerService getInstance(Class<T> clazz){
        if(LoggerService.loggerService == null){
            loggerService = new LoggerService(clazz);
        }

        return LoggerService.loggerService;
    }

    private <T> LoggerService(Class<T> clazz){
        logger = Logger.getLogger(clazz.getName());
    }

    public void info(String message){
        logger.log(Level.INFO, message);
    }

    public void info(String message, Throwable throwable){
        logger.log(Level.INFO, message, throwable);
    }
}
