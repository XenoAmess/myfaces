/*
 * Copyright 2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.myfaces.taglib.core;

import javax.faces.webapp.ConverterELTag;
import javax.faces.convert.Converter;
import javax.faces.context.FacesContext;
import javax.el.ValueExpression;
import javax.el.ELContext;
import javax.servlet.jsp.JspException;

/**
 * Implementation of ConverterELTag
 *
 * @author Bruno Aranda (latest modification by $Author$)
 * @version $Revision$ $Date$
 */
public class ConverterTag extends ConverterELTag
{

    private static final long serialVersionUID = -4506829108081L;
    private ValueExpression _converterId;
    private ValueExpression _binding;

    public ConverterTag()
    {
        super();
    }

    public void setConverterId(ValueExpression converterId)
    {
        _converterId = converterId;
    }

    public void setBinding(ValueExpression binding)
    {
        _binding = binding;
    }

    public void release()
    {
        super.release();
        _converterId = null;
        _binding = null;
    }

    protected Converter createConverter()
            throws JspException
    {
        Converter converter = null;

        FacesContext facesContext = FacesContext.getCurrentInstance();
        ELContext elContext = facesContext.getELContext();

        // try to create the converter from the binding expression first, and then from
        // the converterId
        if (_binding != null)
        {
            try
            {
                converter = (Converter) _binding.getValue(elContext);

                if (converter != null)
                {
                    return converter;
                }
            }
            catch (Exception e)
            {
                throw new JspException("Exception creating converter using binding", e);
            }
        }

        if (_converterId != null)
        {
            try
            {
                String converterId = (String) _converterId.getValue(elContext);
                converter = facesContext.getApplication().createConverter(converterId);

                // with binding no converter was created, set its value with the converter
                // created using the converterId
                if (converter != null && _binding != null)
                {
                    _binding.setValue(elContext, converter);
                }
            }
            catch (Exception e)
            {
                throw new JspException("Exception creating converter with converterId: "+_converterId, e);
            }
        }

        return converter;
    }

}
