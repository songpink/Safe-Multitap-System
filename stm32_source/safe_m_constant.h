#pragma once

#define U_ID_LENGTH 8
#define U_ID "TEST0000"

#define SAFE_M_SQL_API_ERROR '9'
#define SAFE_M_SUCCESS '0'


#define MESSAGE_A_CHAR '0'
#define MESSAGE_B_CHAR '1'


#define RES_A_LENGTH 3
#define RES_B_LENGTH 4
#define RES_MAX_LENGTH 4

#pragma pack(push, 1)
struct MessageARequest{
    char type;
    char u_id[U_ID_LENGTH];
};

struct MessageBRequest{
    char type;
    char id[U_ID_LENGTH];
    double tem;
    double hum;
    double dust;
};
#pragma pack(pop)