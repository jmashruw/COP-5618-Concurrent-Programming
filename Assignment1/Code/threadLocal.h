#ifndef THREADLOCAL_H_
#define THREADLOCAL_H_

#include <iostream>
#include <map>
#include <pthread.h>
#include <algorithm>
#include <mutex>

namespace cop5618 {
    template <typename T>
    class threadLocal {
    public:
        threadLocal() {};
        ~threadLocal() {};

        //disable copy, assign, move, and move assign constructors
        threadLocal(const threadLocal & ) = delete;
        threadLocal & operator = (const threadLocal & ) = delete;
        threadLocal(threadLocal && ) = delete;
        threadLocal & operator = (const threadLocal && ) = delete;

        /* Returns a reference to the current thread's value. If no value has been previously set by this thread, an out_of_range exception is thrown */
        const T& get() const {
            std::unique_lock<std::mutex> myLock (dataMutex);
            if (data_map.find(pthread_self()) != data_map.end())
                return data_map.find(pthread_self()) -> second;
            else throw std::runtime_error("out_of_range");
        }

        /* Sets the value of the threadLocal for the current thread to val */
        void set(T val) {
            std::unique_lock<std::mutex> myLock (dataMutex);
            data_map[pthread_self()] = val;
        }

        /* Removes the current thread's value for the threadLocal */
        void remove() {
            std::unique_lock<std::mutex> myLock (dataMutex);
            if (data_map.find(pthread_self()) != data_map.end())
                data_map.erase(pthread_self());
            else throw std::runtime_error("No such element found");
        }

        /* Friend function. Useful for debugging only, shows values for all threads */
        template <typename U>
        friend std::ostream & operator << (std::ostream & output,
                                           const threadLocal <U> &in) {
            std::unique_lock<std::mutex> myLock (in.dataMutex);
            for (auto i = in.data_map.begin(); i != in.data_map.end(); ++i) {
                output <<"Thread ID: " << i->first << " - " << i->second << std::endl;
            }
            return output;
        }

    private:
        mutable std::mutex dataMutex;
        typedef std::map <pthread_t, T> dataMap;
        dataMap data_map;
    };
} /* namespace cop5618 */
#endif /* THREADLOCAL_H_ */
