#ifndef __existing_string_buf_h
#define __existing_string_buf_h

#include <string>
#include <iostream>

namespace std {

class existing_string_buf : public std::streambuf
{
public:
    // Somehow store a pointer to to_append.
    explicit existing_string_buf(std::string &to_append) : 
        m_to_append(&to_append){}

    virtual int_type overflow (int_type c) {
        if (c != EOF) {
            m_to_append->push_back(c);
        }
        return c;
    }

    virtual std::streamsize xsputn (const char* s, std::streamsize n) {
        m_to_append->insert(m_to_append->end(), s, s + n);                                                                                 
        return n;
    }

private:
    std::string *m_to_append;
};


// std::existing_string_buf;

}

#endif

// __existing_string_buf_h
