FROM debian:bookworm
LABEL org.opencontainers.image.authors="Marco Rodriguez <mrodriguezmad@uoc.edu>"
LABEL org.opencontainers.image.version="0.1"

ARG UNAME
ARG UID
ARG GID
ARG KVMGID

ENV ANDROID_HOME="/opt/android-sdk"
ENV PATH="${PATH}:${ANDROID_HOME}/cmdline-tools/latest/bin:${ANDROID_HOME}/platform-tools"
ENV ANDROID_CMD_TOOLS_VERSION="11076708" 
ENV ANDROID_BUILD_TOOLS_VERSION="35.0.0"
ENV ANDROID_PLATFORM_VERSION="android-35"
ENV ANDROID_SYSTEM_IMAGE="system-images;android-35;google_apis;x86_64"

RUN dpkg --add-architecture i386 && \
    apt-get update && \
    apt-get install -y \
    openjdk-17-jdk \
    binutils \
    wget \
    unzip \
    git \
    libstdc++6:i386 \
    zlib1g:i386 \
    vim \
    # Dependencias del emulador
    libqt5widgets5 libqt5gui5 libqt5core5a libx11-6 libxcomposite1 \
    libxcursor1 libxext6 libxi6 libxrender1 libxtst6 \
    libpulse0 libnss3 libxcomposite1 libxcursor1 \
    libpulse0 libnss3 libasound2 \
    libxkbcommon-x11-0 \
    libxcb-icccm4 \
    libxcb-image0 \
    libxcb-keysyms1 \
    libxcb-randr0 \
    libxcb-render-util0 \
    libxcb-xinerama0 \
    libxcb-shape0 \
    libgl1-mesa-glx \
    libgl1-mesa-dri \    
    && rm -rf /var/lib/apt/lists/*

RUN mkdir -p ${ANDROID_HOME}/cmdline-tools \
    && wget -q https://dl.google.com/android/repository/commandlinetools-linux-${ANDROID_CMD_TOOLS_VERSION}_latest.zip -O /tmp/cmdline-tools.zip \
    && unzip -q /tmp/cmdline-tools.zip -d ${ANDROID_HOME}/cmdline-tools \
    && mv ${ANDROID_HOME}/cmdline-tools/cmdline-tools ${ANDROID_HOME}/cmdline-tools/latest \
    && rm /tmp/cmdline-tools.zip

RUN yes | sdkmanager --licenses \
    && sdkmanager "platform-tools" \
        "platforms;${ANDROID_PLATFORM_VERSION}" \
        "build-tools;${ANDROID_BUILD_TOOLS_VERSION}" \
        "emulator" \
        "${ANDROID_SYSTEM_IMAGE}"

RUN groupadd -r kvm -g $KVMGID
RUN groupadd -g $GID $UNAME
RUN useradd -m -u $UID -g $GID -s /bin/bash $UNAME
RUN gpasswd -a $UNAME kvm
RUN echo "export PATH=$PATH:${ANDROID_HOME}/emulator:/work" >> /home/$UNAME/.bashrc
RUN echo "export ANDROID_HOME=${ANDROID_HOME}" >> /home/$UNAME/.bashrc
RUN chown $UNAME:$UNAME -R /home/$UNAME
RUN chown $UNAME:$UNAME -R ${ANDROID_HOME}

USER $UNAME
RUN echo "no" | avdmanager create avd -n DockerPhone -k "${ANDROID_SYSTEM_IMAGE}" --force

WORKDIR /work

COPY gradlew .
COPY gradle gradle
RUN chmod +x gradlew && ./gradlew --version || true

# Comando por defecto
#CMD ["./gradlew", "tasks"]
#CMD ["/work/gradlew", "build"]
ENTRYPOINT ["/work/gradlew"]