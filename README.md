# 프로젝트 개요

오래 된 멀티탭, 허용 용량 초과, 습도 높은 환경 및 먼지로 인한 멀티탭 화재 사고가 끊이지 않고 발생하고 있습니다. 본 프로젝트에서는 화재 원인들을 감지하고, 사용자에게 알림을 보내는 멀티탭 시스템을 개발하였습니다.

# 소스

### 안드로이드 어플리케이션

[](https://github.com/songpink/Safe-Multitap-System/tree/master/AndroidApplication)

### 서버

[](https://github.com/songpink/Safe-Multitap-System/tree/master/c_server)

### 마이크로 컨트롤러

[](https://github.com/songpink/Safe-Multitap-System/tree/master/stm32_source)

# 요약

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/a0ebfd49-c991-4e27-b317-84a47cac33a6/af4d198b-e595-40f5-b51b-70bbeba457fc/Untitled.png)

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/a0ebfd49-c991-4e27-b317-84a47cac33a6/37469a94-289a-430b-93ff-29305c78343c/Untitled.jpeg)

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/a0ebfd49-c991-4e27-b317-84a47cac33a6/0a30a14c-7f8c-4de2-a0a5-e32c4e68b2fb/Untitled.jpeg)

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/a0ebfd49-c991-4e27-b317-84a47cac33a6/1385880f-5e78-4f85-a31a-69b493294e2c/Untitled.jpeg)

1. 전자기기 사용 시 화재의 원인이 되는 온도, 습도, 먼지 농도를 멀티탭에서 자체적으로 측정해 사용자에게 알림을 줍니다.
2. 스마트폰을 통해 원격으로 멀티탭 전원의 ON/OFF를 제어합니다.
3. 멀티탭 주변을 실시간으로 촬영해 벌레 혹은 곰팡이가 있을 시 사용자에게 알림을 줍니다.

# 담당한 부분

- 프로젝트 팀장
- MCU파트 - 미세먼지 센서 구동 코드 작성
- 안드로이드 어플리케이션 개발

# 성장한 부분

- 코틀린을 사용하여 안드로이드 어플리케이션을 처음으로 개발해 보았고 AOS의 동작 원리에 대해 이해하게 되었습니다.
- 실제 사용되는 멀티탭 내부의 회로 구성에 대해 파악하고 아두이노 모듈을 부착해 사용하는 법을 알게 되었습니다.
- IOT 프로젝트를 하며 일어나게 되는 다양한 하드웨어 오류들을 발견하고 이를 해결해 보는 경험을 하였습니다.
- Mbed OS를 활용해 MCU를 프로그램 하는 법을 자세히 알게 되었습니다.
