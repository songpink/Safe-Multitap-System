#include "mbed.h"
#include "relay_control_function.h"
#include "user_sem.h"
#include "safe_m_constant.h"
#include "network_threads.h"
#include "pin_setting.h"
#include "sensor_function.h"
#include "base64.h"

extern BufferedSerial with_esp;

void get_relay_req_thread_function(void)
{
    while(true){
        printf("relay thread entered\r\n");
        int read_err = 0;
        char relay_req = 0;
        read_err = with_esp.read(&relay_req, sizeof(relay_req));
        if(read_err>0){
            change_relay(relay_req);
            printf("req : %c ", relay_req);
        }
    }
}

void send_data_thread_function(void)
{
    while(true){
        printf("send thread entered\r\n");
        double tem;double hum;
        get_tem_hum(&tem, &hum);
        double dust = get_dust();
        printf("t: %d, h: %d, d: %d\r\n", (int)tem, (int)hum, (int)dust);
        double tx_packet[3];
        tx_packet[0] = tem;
        tx_packet[1] = hum;
        tx_packet[2] = dust;

        uint8_t encoded[33] = {0};
        size_t olen = 0;
        int base64_err = mbedtls_base64_encode(encoded, sizeof(encoded),  &olen, (uint8_t *)tx_packet, sizeof(tx_packet));
        if(base64_err != 0){
            continue;
        }
        encoded[32] = '\0';
        printf("I Send : %s \r\n", encoded);
        char start_signal = '\001';
        with_esp.write(&start_signal, 1);
        with_esp.write(encoded, 32);
        char end_signal = '\002';
        with_esp.write(&end_signal, 1);
        thread_sleep_for(1000);
    }
}