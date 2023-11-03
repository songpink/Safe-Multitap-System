#include "mbed.h"
#include "DHT.h"
#include "internal_sensor_function.h"
#include "pin_setting.h"


#define U16_RES (3.3 / 65536)

static DigitalOut dust_sensor_led(DUST_SENSOR_LED_PIN, 1);
static AnalogIn dust_sensor(DUST_SENSOR_READ_PIN);

static DHT dht_module(DHT_PIN, DHT11);

static Semaphore sem_dht(0, 1);

double _get_dust(void)
{
    dust_sensor_led = 0;
    wait_us(280);
    double raw_voltage = dust_sensor.read() * 3.3;
    wait_us(40);
    dust_sensor_led = 1;
    return raw_voltage;
}


int _get_tem_hum(double * tem, double * hum)
{
    if(dht_module.readData()==0){
        *tem = dht_module.ReadTemperature(CELCIUS);
        *hum = dht_module.ReadHumidity();
        return DHT_SUCCESS;
    }
    else{
        return DHT_ERROR;
    }
}

