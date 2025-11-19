FROM mgsx/libgdx
#MAINTAINER Marco Rodriguez: 0.1
LABEL org.opencontainers.image.authors="Marco Rodriguez <mrodriguezmad@uoc.edu>"
LABEL org.opencontainers.image.version="0.1"

ARG UNAME
ARG UID
ARG GID

# Source - https://stackoverflow.com/a
# Posted by atlas
# Retrieved 2025-11-17, License - CC BY-SA 3.0

RUN wget https://dl.google.com/android/android-sdk_r24.4.1-linux.tgz && \
    tar xzf android-sdk_r24.4.1-linux.tgz && \
    rm android-sdk_r24.4.1-linux.tgz && \
    (echo y | android-sdk-linux/tools/android -s update sdk --no-ui --filter platform-tools,tools -a ) && \
    (echo y | android-sdk-linux/tools/android -s update sdk --no-ui --filter extra-android-m2repository,extra-android-support,extra-google-google_play_services,extra-google-m2repository -a) && \
    (echo y | android-sdk-linux/tools/android -s update sdk --no-ui --filter build-tools-23.0.2,android-24 -a)

RUN groupadd -g $GID $UNAME
RUN useradd -m -u $UID -g $GID -s /bin/bash $UNAME

RUN mkdir -p /home/marco/Android
RUN mv android-sdk-linux /home/marco/Android/Sdk
RUN mkdir -p /home/marco/Android/Sdk/licenses
RUN echo "24333f8a63b6825ea9c5514f83c2829b004d1fee" > /home/marco/Android/Sdk/licenses/android-sdk-license
RUN echo "export PATH=$PATH:/work" >> /home/$UNAME/.bashrc

RUN chown $UNAME:$UNAME -R /home/$UNAME

USER $UNAME
WORKDIR /work

#ENTRYPOINT ["gradlew", "build"]
#CMD ["/usr/bin/bash"]
CMD ["/work/gradlew", "build"]