package com.dataguise.saas.source;

import com.dataguise.saas.exception.BadGatewayException;
import com.dataguise.saas.exception.BadRequestException;
import com.dataguise.saas.exception.InternalServerException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

public interface Source {

     Integer add(SourceDTO sourceDTO) throws BadGatewayException, BadRequestException, InternalServerException;
     Integer edit(Integer id,SourceDTO sourceDTO) throws BadGatewayException, BadRequestException, InternalServerException;
     boolean test(Integer id) throws BadGatewayException, BadRequestException, InternalServerException;
     Integer delete(Integer id) throws BadGatewayException, BadRequestException, InternalServerException;
     SourceDTO get(Integer id) throws BadGatewayException, BadRequestException, InternalServerException;
}
