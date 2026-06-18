import React from 'react';
import { Navigate, Outlet } from 'react-router-dom';

const isValidToken = (token) => {
  if (!token) return false;
  const parts = token.split('.');
  return parts.length === 3;
};

export const PrivateRoute = () => {
  const token = localStorage.getItem('accessToken');
  return isValidToken(token) ? <Outlet /> : <Navigate to="/login" />;
};

export const AdminRoute = () => {
  const token = localStorage.getItem('accessToken');
  const role = localStorage.getItem('userRole');
  return isValidToken(token) && role === 'ADMIN' ? <Outlet /> : <Navigate to="/dashboard" />;
};