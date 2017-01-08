#include <iostream>
#include <pthread.h>
#include <mutex>
#include "threadLocal.h"
#include <string.h>

typedef float customType;

int increment_counter = 0;

int nTHREADS = 50;

std::mutex ioMutex;

struct data {
    std::string t_name;
    customType info;
};

cop5618::threadLocal <customType> t;

void * set(void * input);
void * get(void * input);
void * remove(void * input);
void * setAndGet(void * input);
void * getAndRemove(void * input);
void * removeAndSet(void * input);
void * getAndSet(void * input);
void * setAndRemove(void * input);
void * removeAndGet(void * input);
void * setAndSet(void * input);
void * getSetAndRemove(void * input);
void * setGetAndRemove(void * input);
void * removeAndGetAndSet(void * input);
void * removeAndSetAndGet(void * input);
void tryGet(struct data * input);
void trySet(struct data  * input);
void tryRemove(struct data  * input);

int test_threadLocal() {
    int test[50] ;
    int rc;

    pthread_t myThreads[50];

    struct data s[50];

    for (int i = 0; i < nTHREADS; i++) {
        s[i].t_name = "myThreads" + std::to_string(i);
        s[i].info = 0.1 * i;
    }

    // Set a value where threads does not have a preexisting value
    test[0] = pthread_create( &myThreads[0] , NULL, set, (void * ) &s[0]);
    test[1] = pthread_create( &myThreads[1] , NULL, set, (void * ) &s[1]);

    // Set a value where 1 thread does not have a preexisting value and other thread has a prexisting value
    test[2] = pthread_create( &myThreads[2] , NULL, set, (void * ) &s[2]);
    test[3] = pthread_create( &myThreads[3], NULL, setAndSet, (void * ) &s[3]);

    //Set a value where 1 thread does not have a preexisting value and try to get on other thread
    test[4] = pthread_create( &myThreads[4] , NULL, set, (void * ) &s[4]);
    test[5] = pthread_create( &myThreads[5] , NULL, get, (void * ) &s[5]);

    // Set a value where 1 thread does not have a preexisting value and try to get on existing thread
    test[6] = pthread_create( &myThreads[6] , NULL, set, (void * ) &s[6]);
    test[7] = pthread_create( &myThreads[7] , NULL, setAndGet, (void * ) &s[7]);

    //Set a value where 1 thread does not have a preexisting value and try to remove on other thread
    test[8] = pthread_create( &myThreads[8] , NULL, set, (void * ) &s[8]);
    test[9] = pthread_create( &myThreads[9] , NULL, setAndRemove, (void * ) &s[9]);

    //Set value where thread has preexisting value
    test[10] = pthread_create( &myThreads[10], NULL, set, (void * ) &s[10]);
    test[11] = pthread_create( &myThreads[11], NULL, remove, (void * ) &s[11]);

    //Set a value where threads has a prexisting value
    test[12] = pthread_create( &myThreads[12], NULL, setAndSet, (void * ) &s[12]);
    test[13] = pthread_create( &myThreads[13], NULL, setAndSet, (void * ) &s[13]);

    //Set a value where threads has a prexisting value and attempt to read non existing value
    test[14] = pthread_create( &myThreads[14], NULL, setAndSet, (void * ) &s[14]);
    test[15] = pthread_create( &myThreads[15] , NULL, get, (void * ) &s[15]);

    //Set a value where threads has a prexisting value and attempt to read existing value
    test[16] = pthread_create( &myThreads[16], NULL, setAndSet, (void * ) &s[16]);
    test[17] = pthread_create( &myThreads[17] , NULL, setAndGet, (void * ) &s[17]);

    //Set a value where threads has a prexisting value and attempt to remove existing value
    test[18] = pthread_create( &myThreads[18], NULL, setAndSet, (void * ) &s[18]);
    test[19] = pthread_create( &myThreads[19] , NULL, setAndRemove, (void * ) &s[19]);

    //Set a value where threads has a prexisting value and attempt to remove non existing value
    test[20] = pthread_create( &myThreads[20], NULL, setAndSet, (void * ) &s[20]);
    test[21] = pthread_create( &myThreads[21] , NULL, remove, (void * ) &s[21]);

    //Attempt to read non existing value
    test[22] = pthread_create( &myThreads[22], NULL, get, (void * ) &s[22]);
    test[23] = pthread_create( &myThreads[23] , NULL, get, (void * ) &s[23]);

    //Attempt to read non existing value and attempt to read existing value
    test[24] = pthread_create( &myThreads[24], NULL, get, (void * ) &s[24]);
    test[25] = pthread_create( &myThreads[25] , NULL, setAndGet, (void * ) &s[25]);

    //Attempt to read non existing value and attempt to remove existing value
    test[26] = pthread_create( &myThreads[26], NULL, get, (void * ) &s[26]);
    test[27] = pthread_create( &myThreads[27] , NULL, setAndRemove, (void * ) &s[27]);

    //Attempt to read non existing value and attempt to remove non existing value
    test[28] = pthread_create( &myThreads[28], NULL, get, (void * ) &s[28]);
    test[29] = pthread_create( &myThreads[29] , NULL, remove, (void * ) &s[29]);

    //Attempt to read existing value
    test[30] = pthread_create( &myThreads[30], NULL, setAndGet, (void * ) &s[30]);
    test[31] = pthread_create( &myThreads[31] , NULL, setAndGet, (void * ) &s[31]);

    //Attempt to read existing value and attempt to remove existing value
    test[32] = pthread_create( &myThreads[32], NULL, setAndGet, (void * ) &s[32]);
    test[33] = pthread_create( &myThreads[33] , NULL, setAndRemove, (void * ) &s[33]);

    //Attempt to read existing value and attempt to remove non existing value
    test[34] = pthread_create( &myThreads[34], NULL, setAndGet, (void * ) &s[34]);
    test[35] = pthread_create( &myThreads[35] , NULL, remove, (void * ) &s[35]);

    //Remove a value
    test[36] = pthread_create( &myThreads[36], NULL, setAndRemove, (void * ) &s[36]);
    test[37] = pthread_create( &myThreads[37] , NULL, setAndRemove, (void * ) &s[37]);

    //Remove a value and attempt to remove non existing value
    test[38] = pthread_create( &myThreads[38], NULL, setAndRemove, (void * ) &s[38]);
    test[39] = pthread_create( &myThreads[39] , NULL, remove, (void * ) &s[39]);

    //Attempt to remove non existing value
    test[40] = pthread_create( &myThreads[40], NULL, remove, (void * ) &s[40]);
    test[41] = pthread_create( &myThreads[41] , NULL, remove, (void * ) &s[41]);

    //Basic Test cases:
    test[42] = pthread_create( &myThreads[42], NULL, getSetAndRemove, (void * ) &s[42]);
    test[43] = pthread_create( &myThreads[43], NULL, setGetAndRemove, (void * ) &s[43]);
    test[44] = pthread_create( &myThreads[44], NULL, removeAndGetAndSet, (void * ) &s[44]);
    test[45] = pthread_create( &myThreads[45], NULL, removeAndSetAndGet, (void * ) &s[45]);
    test[46] = pthread_create( &myThreads[46], NULL, getAndRemove, (void * ) &s[46]);
    test[47] = pthread_create( &myThreads[47], NULL, removeAndSet, (void * ) &s[47]);
    test[48] = pthread_create( &myThreads[48], NULL, getAndSet, (void * ) &s[48]);
    test[49] = pthread_create( &myThreads[49], NULL, removeAndGet, (void * ) &s[49]);

    for( int i=0; i < nTHREADS; i++ ) {
        rc = pthread_join(myThreads[i], NULL);
        if (rc) {
            std::unique_lock<std::mutex> lck (ioMutex);
            std::cout << "Unable to join : " << s[i].t_name << " - " << s[i].info << std::endl;
            continue;
        }
    }

    std::cout<<t<<std::endl;

    return increment_counter;
}

void * set(void * input){
    struct data * so = (struct data * ) input;
    trySet(so);
}

void * get(void * input){
    struct data * so = (struct data * ) input;
    tryGet(so);
}

void * remove(void * input){
    struct data * so = (struct data * ) input;
    tryRemove(so);
}

void * setAndGet(void * input) {
    struct data * so = (struct data * ) input;
    trySet(so);
    tryGet(so);
}

void * getAndRemove(void * input) {
    struct data * so = (struct data * ) input;
    tryGet(so);
    tryRemove(so);
}

void * removeAndSet(void * input) {
    struct data * so = (struct data * ) input;
    tryRemove(so);
    trySet(so);
}

void * getAndSet(void * input) {
    struct data * so = (struct data * ) input;
    tryGet(so);
    trySet(so);
}

void * setAndRemove(void * input) {
    struct data * so = (struct data * ) input;
    trySet(so);
    tryRemove(so);
}

void * removeAndGet(void * input) {
    struct data * so = (struct data * ) input;
    tryRemove(so);
    tryGet(so);
}

void * setAndSet(void * input){
    struct data * so = (struct data * ) input;
    trySet(so);
    so->info += 100;
    trySet(so);
}

void * getSetAndRemove(void * input){
    struct data * so = (struct data * ) input;
    tryGet(so);
    trySet(so);
    tryRemove(so);
}

void * setGetAndRemove(void * input){
    struct data * so = (struct data * ) input;
    trySet(so);
    tryGet(so);
    tryRemove(so);
}

void * removeAndGetAndSet(void * input){
    struct data * so = (struct data * ) input;
    tryRemove(so);
    tryGet(so);
    trySet(so);
}

void * removeAndSetAndGet(void * input){
    struct data * so = (struct data * ) input;
    tryRemove(so);
    trySet(so);
    tryGet(so);
}


void trySet(struct data * input) {
    t.set(input->info);
    std::unique_lock<std::mutex> lck (ioMutex);
    std::cout <<"Setting thread: "<<input->t_name <<" value to"  << " - " << t.get() << std::endl;
}

void tryGet(struct data * input) {
    try {
        t.get();
        std::unique_lock<std::mutex> lck (ioMutex);
        std::cout << "Thread " << input->t_name << " has value " << input->info <<std::endl;
    } catch (std::exception &e) {
        std::unique_lock<std::mutex> lck (ioMutex);
        increment_counter += 1;
        std::cout << input->t_name << ": Exception: " << e.what() << std::endl;
    }
}

void tryRemove(struct data * input) {
    try {
        t.remove();
    } catch (std::exception &e) {
        std::unique_lock<std::mutex> lck (ioMutex);
        increment_counter += 1;
        std::cout << input-> t_name << ": Exception: " << e.what() << std::endl;
    }
}