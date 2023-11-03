#pragma once
#include "mbed.h"



#define DHT_ERROR 1
#define DHT_SUCCESS 0


double _get_dust(void);
double _get_current_hole0(void);
double _get_current_hole1(void);
double _get_current_hole2(void);
double _get_adj_voltage(void);
int _get_tem_hum(double * tem, double * hum);