import React, { useCallback, useState } from 'react';
import { useDropzone } from 'react-dropzone';
import toast from 'react-hot-toast';

const FileUpload = ({ onFilesSelected, multiple = true }) => {
  const [files, setFiles] = useState([]);
  const onDrop = useCallback((acceptedFiles) => {
    setFiles(prev => [...prev, ...acceptedFiles]);
    if (onFilesSelected) onFilesSelected(acceptedFiles);
    toast.success(`${acceptedFiles.length} archivo(s) seleccionado(s)`);
  }, [onFilesSelected]);
  const removeFile = (index) => setFiles(prev => prev.filter((_, i) => i !== index));
  const { getRootProps, getInputProps } = useDropzone({ onDrop });
  return (
    <div>
      <div {...getRootProps()} className="border-2 border-dashed border-gray-300 rounded-lg p-6 text-center cursor-pointer hover:border-primary-600 transition">
        <input {...getInputProps()} />
        <p className="text-gray-500">Arrastra o haz clic para subir documentos</p>
      </div>
      {files.length > 0 && (
        <div className="mt-4">
          <h4 className="font-medium">Archivos seleccionados:</h4>
          <ul className="list-disc pl-5">
            {files.map((file, idx) => (
              <li key={idx} className="flex justify-between items-center">
                <span>{file.name} ({(file.size / 1024).toFixed(2)} KB)</span>
                <button onClick={() => removeFile(idx)} className="text-red-500 text-sm">Eliminar</button>
              </li>
            ))}
          </ul>
        </div>
      )}
    </div>
  );
};

export default FileUpload;