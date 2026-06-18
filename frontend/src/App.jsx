import React, { lazy, Suspense } from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { Toaster } from 'react-hot-toast';
import { ThemeProvider } from './contexts/ThemeContext';
import Layout from './components/layout/Layout';
import { PrivateRoute, AdminRoute } from './components/common/ProtectedRoute';
import LoadingSpinner from './components/common/LoadingSpinner';

const Login = lazy(() => import('./pages/Login'));
const Register = lazy(() => import('./pages/Register'));
const Dashboard = lazy(() => import('./pages/Dashboard'));
const Policies = lazy(() => import('./pages/Policies'));
const Claims = lazy(() => import('./pages/Claims'));
const NewClaim = lazy(() => import('./pages/NewClaim'));
const Insurers = lazy(() => import('./pages/Insurers'));
const ClaimDetail = lazy(() => import('./pages/ClaimDetail'));
const PolicyDetail = lazy(() => import('./pages/PolicyDetail'));
const AdminClaims = lazy(() => import('./pages/admin/AdminClaims'));
const AdminUsers = lazy(() => import('./pages/AdminUsers'));
const AdminInsurers = lazy(() => import('./pages/admin/AdminInsurers'));

const queryClient = new QueryClient({
  defaultOptions: {
    queries: { retry: 1, refetchOnWindowFocus: false, staleTime: 5 * 60 * 1000 },
  },
});

function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <ThemeProvider>
        <BrowserRouter>
          <Toaster position="top-right" toastOptions={{ duration: 4000 }} />
          <Suspense fallback={<LoadingSpinner />}>
            <Routes>
              <Route path="/login" element={<Login />} />
              <Route path="/register" element={<Register />} />
              <Route element={<PrivateRoute />}>
                <Route element={<Layout />}>
                  <Route path="/" element={<Navigate to="/dashboard" />} />
                  <Route path="/dashboard" element={<Dashboard />} />
                  <Route path="/polizas" element={<Policies />} />
                  <Route path="/polizas/:id" element={<PolicyDetail />} />
                  <Route path="/reclamos" element={<Claims />} />
                  <Route path="/reclamos/nuevo" element={<NewClaim />} />
                  <Route path="/reclamos/:id" element={<ClaimDetail />} />
                  <Route path="/aseguradoras" element={<Insurers />} />
                  <Route element={<AdminRoute />}>
                    <Route path="/admin/reclamos" element={<AdminClaims />} />
                    <Route path="/admin/usuarios" element={<AdminUsers />} />
                    <Route path="/admin/aseguradoras" element={<AdminInsurers />} />
                  </Route>
                </Route>
              </Route>
            </Routes>
          </Suspense>
        </BrowserRouter>
      </ThemeProvider>
    </QueryClientProvider>
  );
}

export default App;