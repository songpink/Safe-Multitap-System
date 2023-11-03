#include "mbed.h"
#include "internal_relay_control_function.h"
#include "relay_control_function.h"

#define RELAY_O true
#define RELAY_X false

void change_relay(char pa_relay_status)
{
    if(pa_relay_status==RELAY_XXX){
        _relay0_control(RELAY_X);
        _relay1_control(RELAY_X);
        _relay2_control(RELAY_X);
    }
    else if(pa_relay_status==RELAY_XXO){
        _relay0_control(RELAY_O);
        _relay1_control(RELAY_X);
        _relay2_control(RELAY_X);
    }
    else if(pa_relay_status==RELAY_XOX){
        _relay0_control(RELAY_X);
        _relay1_control(RELAY_O);
        _relay2_control(RELAY_X);
    }
    else if(pa_relay_status==RELAY_XOO){
        _relay0_control(RELAY_O);
        _relay1_control(RELAY_O);
        _relay2_control(RELAY_X);
    }
    else if(pa_relay_status==RELAY_OXX){
        _relay0_control(RELAY_X);
        _relay1_control(RELAY_X);
        _relay2_control(RELAY_O);
    }
    else if(pa_relay_status==RELAY_OXO){
        _relay0_control(RELAY_O);
        _relay1_control(RELAY_X);
        _relay2_control(RELAY_O);
    }
    else if(pa_relay_status==RELAY_OOX){
        _relay0_control(RELAY_X);
        _relay1_control(RELAY_O);
        _relay2_control(RELAY_O);
    }
    else if(pa_relay_status==RELAY_OOO){
        _relay0_control(RELAY_O);
        _relay1_control(RELAY_O);
        _relay2_control(RELAY_O);
    }
    return;
}
