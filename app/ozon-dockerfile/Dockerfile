FROM ubuntu:22.04

# Устанавливаем переменные для автоматического выбора часового пояса
ENV DEBIAN_FRONTEND=noninteractive
ENV TZ=Etc/UTC

# Установка Xfce, Java, Chrome и VNC (без интерактивных запросов)
RUN apt-get update && apt-get install -y \
    xfce4 \
    xfce4-goodies \
    xorg \
    openjdk-21-jdk \
    wget \
    curl \
    x11vnc \
    xvfb \
    gnupg \
    tzdata \
    && ln -fs /usr/share/zoneinfo/$TZ /etc/localtime \
    && dpkg-reconfigure --frontend noninteractive tzdata \
    && rm -rf /var/lib/apt/lists/*

# Установка Google Chrome (правильная версия)
RUN wget -q -O - https://dl.google.com/linux/linux_signing_key.pub | gpg --dearmor -o /usr/share/keyrings/googlechrome-linux-keyring.gpg \
    && echo "deb [arch=amd64 signed-by=/usr/share/keyrings/googlechrome-linux-keyring.gpg] http://dl.google.com/linux/chrome/deb/ stable main" > /etc/apt/sources.list.d/google-chrome.list \
    && apt-get update \
    && apt-get install -y google-chrome-stable \
    && rm -rf /var/lib/apt/lists/*

# Установка переменных окружения
ENV SPRING_PROFILES_ACTIVE=prod \
    DISPLAY=:99 \
    LANG=C.UTF-8 \
    VNC_PASSWORD=123456 \
    OZON_ENABLED=true \
    OZON_MAX_PROCESSING_THREADS=1 \
    OZON_PARSER_MODE=headless \
    OZON_PARSER_WEB_DRIVER_URL_CATALOG=http://localhost:4441/wd/hub \
    OZON_PARSER_WEB_DRIVER_URL_CHARACTERISTICS=http://localhost:4442/wd/hub \
    POSTGRES_JDBC_PASSWORD=postgres \
    POSTGRES_JDBC_URL=postgres:5432/ne-catalog \
    POSTGRES_JDBC_USERNAME=postgres \
    SERVER_PORT=8080

# Копируем заранее собранный JAR-файл
# Предположим, что он лежит рядом с Dockerfile
COPY *.jar /opt/myapp/MyApp.jar

# Запуск приложения с виртуальным дисплеем
CMD (\
    rm -f /tmp/.X99-lock && \
    Xvfb $DISPLAY -screen 0 1920x1080x24 -ac +extension GLX +render -noreset & \
    sleep 2 && \
    startxfce4 & \
    x11vnc -display $DISPLAY -forever -shared -passwd "$VNC_PASSWORD" & \
    google-chrome --no-sandbox --disable-dev-shm-usage --start-maximized "https://www.google.com" & \
    java -Xmx6096m -Xms3024m -jar /opt/myapp/MyApp.jar \
)