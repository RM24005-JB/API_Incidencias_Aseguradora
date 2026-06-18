import React, { useState } from 'react';
import { useForm } from 'react-hook-form';
import { useNavigate } from 'react-router-dom';
import { usePolicies } from '../hooks/usePolicies';
import { useClaims } from '../hooks/useClaims';
import FileUpload from '../components/common/FileUpload';
import { useTranslation } from 'react-i18next';
import toast from 'react-hot-toast';
import { motion, AnimatePresence } from 'framer-motion';

const NewClaim = () => {
  const { t } = useTranslation();
  const navigate = useNavigate();
  const { policies } = usePolicies(0, 100);
  const { createClaim } = useClaims();
  const [step, setStep] = useState(1);
  const [polizaId, setPolizaId] = useState('');
  const [formData, setFormData] = useState({});
  const [files, setFiles] = useState([]);
  const { register, handleSubmit, formState: { errors } } = useForm();

  const onSelectPoliza = (data) => {
    setPolizaId(data.polizaId);
    setStep(2);
  };

  const onSubmitDetails = (data) => {
    setFormData(data);
    setStep(3);
  };

  const handleFinalSubmit = async () => {
    try {
      const payload = {
        polizaId: parseInt(polizaId),
        fechaSiniestro: formData.fechaSiniestro,
        descripcion: formData.descripcion,
        montoEstimado: parseFloat(formData.montoEstimado),
        files: files,
      };
      await createClaim.mutateAsync(payload);
      navigate('/reclamos');
    } catch (err) {
      toast.error(err.response?.data?.message || t('error'));
    }
  };

  const stepVariants = {
    hidden: { opacity: 0, x: 50 },
    visible: { opacity: 1, x: 0 },
    exit: { opacity: 0, x: -50 }
  };

  return (
    <div className="max-w-2xl mx-auto">
      <h1 className="text-2xl font-bold mb-4">{t('newClaim')}</h1>
      <div className="mb-8 flex items-center gap-2">
        {[1, 2, 3].map((s) => (
          <React.Fragment key={s}>
            <motion.div 
              className={`flex items-center justify-center w-8 h-8 rounded-full text-sm font-medium
                ${step >= s ? 'bg-primary-600 text-white' : 'bg-gray-200 text-gray-600'}`}
              animate={{ scale: step === s ? 1.1 : 1 }}
              transition={{ type: "spring", stiffness: 300 }}
            >{s}</motion.div>
            {s < 3 && (
              <div className="flex-1 h-1 bg-gray-200 rounded">
                <motion.div 
                  className="h-full bg-primary-600 rounded"
                  initial={{ width: 0 }}
                  animate={{ width: step > s ? '100%' : step === s ? '50%' : '0%' }}
                  transition={{ duration: 0.3 }}
                />
              </div>
            )}
          </React.Fragment>
        ))}
      </div>

      <AnimatePresence mode="wait">
        {step === 1 && (
          <motion.form key="step1" variants={stepVariants} initial="hidden" animate="visible" exit="exit"
            transition={{ duration: 0.3 }} onSubmit={handleSubmit(onSelectPoliza)} 
            className="bg-white dark:bg-gray-800 p-6 rounded-xl shadow-md">
            <label className="block mb-2 font-medium">{t('selectPolicy')}</label>
            <select {...register('polizaId', { required: true })} 
              className="w-full border p-2 rounded mb-4 dark:bg-gray-700">
              <option value="">-- {t('selectPolicy')} --</option>
              {policies.map(p => (
                <option key={p.id} value={p.id}>{p.nombreAseguradora} - {p.numeroPoliza} ({p.tipo})</option>
              ))}
            </select>
            {errors.polizaId && <p className="text-red-500 text-sm mb-4">{t('required')}</p>}
            <button type="submit" className="bg-primary-600 text-white px-4 py-2 rounded-lg hover:bg-primary-700 transition">
              {t('next')}
            </button>
          </motion.form>
        )}

        {step === 2 && (
          <motion.form key="step2" variants={stepVariants} initial="hidden" animate="visible" exit="exit"
            transition={{ duration: 0.3 }} onSubmit={handleSubmit(onSubmitDetails)} 
            className="bg-white dark:bg-gray-800 p-6 rounded-xl shadow-md space-y-4">
            <div>
              <label className="block mb-1 font-medium">{t('claimDate')}</label>
              <input type="datetime-local" {...register('fechaSiniestro', { required: true })} 
                className="w-full border p-2 rounded dark:bg-gray-700" />
              {errors.fechaSiniestro && <p className="text-red-500 text-sm">{t('required')}</p>}
            </div>
            <div>
              <label className="block mb-1 font-medium">{t('description')}</label>
              <textarea {...register('descripcion', { required: true })} rows="4" 
                className="w-full border p-2 rounded dark:bg-gray-700" />
              {errors.descripcion && <p className="text-red-500 text-sm">{t('required')}</p>}
            </div>
            <div>
              <label className="block mb-1 font-medium">{t('estimatedAmount')}</label>
              <input type="number" step="0.01" {...register('montoEstimado', { required: true, min: 0 })} 
                className="w-full border p-2 rounded dark:bg-gray-700" />
              {errors.montoEstimado && <p className="text-red-500 text-sm">{t('positiveNumber')}</p>}
            </div>
            <div className="flex gap-2">
              <button type="button" onClick={() => setStep(1)} 
                className="bg-gray-500 text-white px-4 py-2 rounded-lg hover:bg-gray-600 transition">{t('previous')}</button>
              <button type="submit" 
                className="bg-primary-600 text-white px-4 py-2 rounded-lg hover:bg-primary-700 transition">{t('next')}</button>
            </div>
          </motion.form>
        )}

        {step === 3 && (
          <motion.div key="step3" variants={stepVariants} initial="hidden" animate="visible" exit="exit"
            transition={{ duration: 0.3 }} className="bg-white dark:bg-gray-800 p-6 rounded-xl shadow-md space-y-4">
            <p className="font-medium">{t('uploadDocuments')}</p>
            <p className="text-sm text-gray-500">{t('fileTypes')}</p>
            <FileUpload onFilesSelected={setFiles} />
            <div className="flex gap-2">
              <button onClick={() => setStep(2)} 
                className="bg-gray-500 text-white px-4 py-2 rounded-lg hover:bg-gray-600 transition">{t('previous')}</button>
              <button onClick={handleFinalSubmit} 
                className="bg-primary-600 text-white px-4 py-2 rounded-lg hover:bg-primary-700 transition">{t('submitClaim')}</button>
            </div>
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  );
};

export default NewClaim;