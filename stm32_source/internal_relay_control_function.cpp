#include "mbed.h"
#include "pin_setting.h"

DigitalOut relay0(RELAY_0_PIN, false);
DigitalOut relay1(RELAY_1_PIN, false);
DigitalOut relay2(RELAY_2_PIN, false);

void _relay0_control(bool out)
{
    relay0.write(out);
}

void _relay1_control(bool out)
{
    relay1.write(out);
}

void _relay2_control(bool out)
{
    relay2.write(out);
}