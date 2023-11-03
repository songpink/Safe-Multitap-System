#include <mbed.h>
#include "pin_setting.h"
#include "network_threads.h"

BufferedSerial with_esp( MY_TX, MY_RX);
Thread get_relay_req_thread(osPriorityNormal, 2048);
Thread send_data_thread(osPriorityNormal, 2048);


int main()
{
    with_esp.set_baud(115200);
    //with_esp.set_flow_control(BufferedSerial::RTSCTS, RTS_PIN, CTS_PIN);
    get_relay_req_thread.start(&get_relay_req_thread_function);
    send_data_thread.start(send_data_thread_function);
}
