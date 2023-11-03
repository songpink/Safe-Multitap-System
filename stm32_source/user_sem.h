#pragma once
#include "mbed.h"

extern Semaphore sem_energy_thread;

extern Semaphore sem_tem_hum_thread;

extern Semaphore sem_dust_thread;

extern Semaphore sem_packet_sender_thread;

extern Semaphore sem_message_a;

extern Semaphore sem_wait_for_res;