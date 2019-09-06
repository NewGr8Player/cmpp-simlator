import ch.qos.logback.classic.db.DBAppender
import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.classic.filter.LevelFilter
import ch.qos.logback.core.db.DriverManagerConnectionSource
import org.springframework.boot.logging.logback.ColorConverter
import org.springframework.boot.logging.logback.ExtendedWhitespaceThrowableProxyConverter
import org.springframework.boot.logging.logback.WhitespaceThrowableProxyConverter

import java.nio.charset.Charset

import static ch.qos.logback.classic.Level.*
import static ch.qos.logback.core.spi.FilterReply.ACCEPT
import static ch.qos.logback.core.spi.FilterReply.DENY

def LOG_HOME = "./logs"
conversionRule("clr", ColorConverter)
conversionRule("wex", WhitespaceThrowableProxyConverter)
conversionRule("wEx", ExtendedWhitespaceThrowableProxyConverter)
def LOG_FILE_NAME = "%d{yyyy-MM-dd}.%i.log"
def SPLIT_FILE_SIZE = "1MB"
def FILE_LOG_PATTERN = "%d{yyyy-MM-dd HH:mm:ss.SSS} %-5level --- [%15.15thread] %-40.40logger{39} : %msg%n"
def CONSOLE_LOG_PATTERN = "%clr(%d{yyyy-MM-dd HH:mm:ss.SSS}){faint} %clr(%-5level) %clr(---){faint} %clr([%15.15thread]){faint} %clr(%-40.40logger{39}){cyan} %clr(:){faint} %m%n"
appender("STDOUT", ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = CONSOLE_LOG_PATTERN
        charset = Charset.forName("utf8")
    }
}

appender("TRACE_FILE", RollingFileAppender) {
    rollingPolicy(SizeAndTimeBasedRollingPolicy) {
        fileNamePattern = LOG_HOME + "/trace/" + LOG_FILE_NAME
        maxFileSize = SPLIT_FILE_SIZE
    }
    encoder(PatternLayoutEncoder) {
        pattern = FILE_LOG_PATTERN
    }
}
appender("INFO_FILE", RollingFileAppender) {
    rollingPolicy(SizeAndTimeBasedRollingPolicy) {
        fileNamePattern = LOG_HOME + '/info/' + LOG_FILE_NAME
        maxFileSize = SPLIT_FILE_SIZE
    }
    encoder(PatternLayoutEncoder) {
        pattern = FILE_LOG_PATTERN
    }
    filter(LevelFilter) {
        level = INFO
        onMatch = ACCEPT
        onMismatch = DENY
    }
}
appender("DEBUG_FILE", RollingFileAppender) {
    rollingPolicy(SizeAndTimeBasedRollingPolicy) {
        fileNamePattern = LOG_HOME + '/debug/' + LOG_FILE_NAME
        maxFileSize = SPLIT_FILE_SIZE
    }
    encoder(PatternLayoutEncoder) {
        pattern = FILE_LOG_PATTERN
    }
    filter(LevelFilter) {
        level = DEBUG
        onMatch = ACCEPT
        onMismatch = DENY
    }
}
appender("WARN_FILE", RollingFileAppender) {
    rollingPolicy(SizeAndTimeBasedRollingPolicy) {
        fileNamePattern = LOG_HOME + '/warn/' + LOG_FILE_NAME
        maxFileSize = SPLIT_FILE_SIZE
    }
    encoder(PatternLayoutEncoder) {
        pattern = FILE_LOG_PATTERN
    }
    filter(LevelFilter) {
        level = WARN
        onMatch = ACCEPT
        onMismatch = DENY
    }
}
appender("ERROR_FILE", RollingFileAppender) {
    rollingPolicy(SizeAndTimeBasedRollingPolicy) {
        fileNamePattern = LOG_HOME + '/error/' + LOG_FILE_NAME
        maxFileSize = SPLIT_FILE_SIZE
    }
    encoder(PatternLayoutEncoder) {
        pattern = FILE_LOG_PATTERN
    }
    filter(LevelFilter) {
        level = ERROR
        onMatch = ACCEPT
        onMismatch = DENY
    }
}
/*appender("LOG_DB", DBAppender) {
    connectionSource(DriverManagerConnectionSource) {
        driverClass = "com.mysql.jdbc.Driver"
        url = "jdbc:mysql://127.0.0.1:3306/log_x"
        user = "root"
        password = "root"
    }
}*/
root(INFO, ["STDOUT", "TRACE_FILE", "INFO_FILE", "DEBUG_FILE", "WARN_FILE", "ERROR_FILE"])