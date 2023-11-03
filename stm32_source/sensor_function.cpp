#include "internal_sensor_function.h"
#include <math.h>


void get_tem_hum(double * tem, double * hum)
{
    while(true){
        if(_get_tem_hum(tem, hum)==DHT_SUCCESS){
            break;
        }
        thread_sleep_for(50);
    }
}

static double trans_dust_raw_to_density(double raw)
{
    return abs(raw - 0.03) * 200; // ((raw - 1.1) / 0.005)
}

double get_dust(void)
{
    double raw_voltage = _get_dust(); // get raw value

    return trans_dust_raw_to_density(raw_voltage);
}
