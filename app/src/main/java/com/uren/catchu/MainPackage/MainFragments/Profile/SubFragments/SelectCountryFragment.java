package com.uren.catchu.MainPackage.MainFragments.Profile.SubFragments;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.uren.catchu.ApiGatewayFunctions.CountryListProcess;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.OnEventListener;
import com.uren.catchu.ApiGatewayFunctions.Interfaces.TokenCallback;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.DialogBoxUtil;
import com.uren.catchu.GeneralUtils.DialogBoxUtil.InfoDialogBoxCallback;
import com.uren.catchu.GeneralUtils.FirebaseHelperModel.ErrorSaveHelper;
import com.uren.catchu.GeneralUtils.ShapeUtil;
import com.uren.catchu.Interfaces.ItemClickListener;
import com.uren.catchu.R;
import com.uren.catchu.Singleton.AccountHolderInfo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import catchu.model.Country;
import catchu.model.CountryListResponse;
import catchu.model.FriendList;
import catchu.model.UserProfileProperties;

import static com.uren.catchu.Constants.StringConstants.PUTEXTRA_PHONE_NUM;
import static com.uren.catchu.MainPackage.MainFragments.BaseFragment.ARGS_INSTANCE;

@SuppressLint("ValidFragment")
public class SelectCountryFragment extends Fragment implements Filterable {

    View mView;

    @BindView(R.id.selectCountryEt)
    EditText selectCountryEt;
    @BindView(R.id.countryListView)
    ListView countryListView;
    @BindView(R.id.mainLinearLayout)
    LinearLayout mainLinearLayout;

    ArrayAdapter<String> countryAdapter;
    List<String> orgCountryList;
    List<String> countryList;
    ItemClickListener listener;
    CountryListResponse countryListResponse;
    Country myCountry;

    public SelectCountryFragment(ItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        try {
            if (mView == null) {
                mView = inflater.inflate(R.layout.fragment_select_country, container, false);
                ButterKnife.bind(this, mView);
            }
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(),SelectCountryFragment.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
            e.printStackTrace();
        }

        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        try {
            super.onViewCreated(view, savedInstanceState);
            init();
            addListeners();
            getCountryList();
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(),SelectCountryFragment.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
            e.printStackTrace();
        }
    }

    private void init() {
        setShapes();
    }

    private void setShapes() {
        try {
            GradientDrawable shape = ShapeUtil.getShape(getActivity().getResources().getColor(R.color.White, null),
                    0, GradientDrawable.RECTANGLE, 15, 0);
            mainLinearLayout.setBackground(shape);
        } catch (Resources.NotFoundException e) {
            ErrorSaveHelper.writeErrorToDB(getContext(),SelectCountryFragment.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
            e.printStackTrace();
        }
    }

    public void addListeners() {
        try {
            selectCountryEt.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    countryAdapter.getFilter().filter(s.toString().trim());
                }
            });

            countryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String selectedCountry = (String) countryListView.getItemAtPosition(position);
                    parseSelectedCountry(selectedCountry);
                    getActivity().onBackPressed();
                }
            });
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(),SelectCountryFragment.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
            e.printStackTrace();
        }
    }

    public void parseSelectedCountry(String selectedCountry) {
        try {
            String[] parts = selectedCountry.split("\\(");

            String[] parts2 = parts[1].split("\\)");
            String countryDialCode = parts2[0];

            for(Country country : countryListResponse.getItems()){
                if(country != null && country.getDialCode() != null && !country.getDialCode().trim().isEmpty()){
                    if(countryDialCode.trim().equals(country.getDialCode())){
                        myCountry = country;
                        break;
                    }
                }
            }
            listener.onClick(myCountry, 0);
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(),SelectCountryFragment.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
            e.printStackTrace();
        }
    }

    public void getCountryList() {
        AccountHolderInfo.getToken(new TokenCallback() {
            @Override
            public void onTokenTaken(String token) {

                CountryListProcess countryListProcess = new CountryListProcess(new OnEventListener() {
                    @Override
                    public void onSuccess(Object object) {
                        if (object != null)
                            fillCountryList(object);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        ErrorSaveHelper.writeErrorToDB(getContext(),SelectCountryFragment.class.getSimpleName(),
                                new Object() {
                                }.getClass().getEnclosingMethod().getName(), e.getMessage());
                        DialogBoxUtil.showErrorDialog(getActivity(), getResources().getString(R.string.error) + e.getMessage(), new InfoDialogBoxCallback() {
                            @Override
                            public void okClick() {
                            }
                        });
                    }

                    @Override
                    public void onTaskContinue() {

                    }
                }, AccountHolderInfo.getInstance().getUser().getUserInfo().getUserid(),
                        token);
                countryListProcess.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        });
    }

    public void fillCountryList(Object object) {
        try {
            countryListResponse = (CountryListResponse) object;
            orgCountryList = new ArrayList<String>();

            for (Country country : countryListResponse.getItems()) {
                String countryItem = "";

                if (country.getName() != null && !country.getName().trim().isEmpty() &&
                        country.getDialCode() != null && !country.getDialCode().trim().isEmpty()) {
                    countryItem = country.getName() + "(" + country.getDialCode() + ")";
                    orgCountryList.add(countryItem);
                }
            }

            countryAdapter = new ArrayAdapter<String>(
                    getActivity(),
                    android.R.layout.simple_list_item_1,
                    orgCountryList);

            countryListView.setAdapter(countryAdapter);
        } catch (Exception e) {
            ErrorSaveHelper.writeErrorToDB(getContext(),SelectCountryFragment.class.getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(), e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String searchString = charSequence.toString();
                if (searchString.trim().isEmpty())
                    countryList = orgCountryList;
                else {
                    List<String> tempCountryList = new ArrayList<>();

                    for (String country : orgCountryList) {
                        if (country.toLowerCase().contains(searchString.toLowerCase()))
                            tempCountryList.add(country);
                    }
                    countryList = tempCountryList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = countryList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                countryList = (List<String>) filterResults.values;
                countryAdapter.notifyDataSetChanged();
            }
        };
    }
}
