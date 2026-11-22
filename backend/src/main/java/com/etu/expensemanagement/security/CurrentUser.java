package com.etu.expensemanagement.security;

/**
 * "Who is calling?" - the only identity question services are allowed to ask.
 */
public interface CurrentUser {

    /** throws UnauthorizedException if no identified */
    Long employeeId();
}
