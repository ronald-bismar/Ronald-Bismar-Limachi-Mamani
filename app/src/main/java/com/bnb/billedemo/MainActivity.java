package com.bnb.billedemo;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.bnb.billedemo.domain.model.UserData;
import com.bnb.billedemo.domain.repository.BilleRepository;
import com.bnb.billedemo.domain.usecase.ValidateUserDataUseCase;
import com.bnb.billedemo.presentation.BilleViewModel;

public final class MainActivity extends ComponentActivity {
    private static final int LOCATION_REQUEST = 41;
    private static final int GREEN = Color.rgb(11, 174, 103);
    private static final int DARK_GREEN = Color.rgb(7, 93, 65);
    private static final int INK = Color.rgb(36, 42, 44);
    private static final int MUTED = Color.rgb(103, 108, 111);
    private static final int PURPLE = Color.rgb(119, 69, 154);

    private FrameLayout root;
    private BilleViewModel viewModel;
    private final ValidateUserDataUseCase validator = new ValidateUserDataUseCase();
    private EditText phoneInput;
    private EditText carnetInput;
    private EditText complementInput;
    private TextView phoneMessage;
    private TextView carnetMessage;
    private TextView complementMessage;
    private CheckBox complementCheck;
    private Button primaryButton;
    private ProgressBar loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(DARK_GREEN);
        viewModel = new ViewModelProvider(this).get(BilleViewModel.class);
        showInformation();
    }

    private void showInformation() {
        root = new FrameLayout(this);
        root.setBackgroundColor(Color.WHITE);

        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.VERTICAL);
        header.setPadding(dp(16), dp(8), dp(16), 0);
        header.setBackgroundColor(GREEN);
        header.addView(topNavigation("Informacion"));
        header.addView(stepHeader("PASO 1 / 4", "Informacion", 1));
        TextView intro = label("Ingresa tus datos", 18, Color.WHITE, Typeface.BOLD);
        header.addView(intro, marginParams(-1, -2, 0, 16, 0, 0));
        TextView helper = label("Necesitamos esta informacion para comenzar a\ndisfrutar de tu bille.", 12, Color.WHITE, Typeface.NORMAL);
        helper.setLineSpacing(0, 1.05f);
        header.addView(helper);
        root.addView(header, frameParams(-1, dp(232), Gravity.TOP));

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(16), dp(12), dp(16), dp(10));
        card.setBackground(round(Color.WHITE, dp(16), dp(16), 0, 0));
        phoneInput = input(InputType.TYPE_CLASS_NUMBER, 8);
        carnetInput = input(InputType.TYPE_CLASS_NUMBER, 10);
        complementInput = input(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS, 2);
        complementInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(2), new AlphanumericInputFilter()});
        complementInput.setEnabled(false);
        phoneMessage = addField(card, "Numero de celular:", phoneInput, "71234567", "Introduzca 8 digitos");
        carnetMessage = addField(card, "Numero de carnet:", carnetInput, "412345", "Introduzca hasta 10 digitos");
        complementMessage = addField(card, "Complemento (opcional):", complementInput, "2 letras o numeros", "Introduzca hasta 2 letras o numeros");
        complementMessage.setVisibility(View.GONE);
        complementCheck = new CheckBox(this);
        complementCheck.setText("Tiene complemento?");
        complementCheck.setTextSize(10);
        complementCheck.setTextColor(MUTED);
        complementCheck.setButtonTintList(checkBoxColors());
        complementCheck.setPadding(0, 0, 0, 0);
        card.addView(complementCheck, marginParams(-1, dp(34), 0, 0, 0, 0));
        complementCheck.setOnCheckedChangeListener((button, checked) -> {
            complementCheck.setTextColor(checked ? GREEN : MUTED);
            complementInput.setEnabled(checked);
            if (!checked) {
                complementInput.setText("");
                complementInput.clearFocus();
            }
        });
        TextWatcher validationWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override public void afterTextChanged(Editable editable) { updateValidationMessages(); }
        };
        phoneInput.addTextChangedListener(validationWatcher);
        carnetInput.addTextChangedListener(validationWatcher);
        complementInput.addTextChangedListener(validationWatcher);
        View.OnFocusChangeListener focusListener = (view, hasFocus) -> updateValidationMessages();
        phoneInput.setOnFocusChangeListener(focusListener);
        carnetInput.setOnFocusChangeListener(focusListener);
        complementInput.setOnFocusChangeListener(focusListener);
        updateValidationMessages();
        root.addView(card, frameParams(-1, -1, Gravity.TOP, 0, dp(170), 0, dp(78)));

        primaryButton = button("Siguiente");
        FrameLayout.LayoutParams buttonParams = frameParams(-1, dp(48), Gravity.BOTTOM, dp(14), 0, dp(14), dp(14));
        root.addView(primaryButton, buttonParams);
        primaryButton.setOnClickListener(v -> continueFromInformation());
        setContentView(root);
        animateIn(root);
    }

    private View topNavigation(String title) {
        FrameLayout nav = new FrameLayout(this);
        TextView back = label("‹", 32, Color.WHITE, Typeface.NORMAL);
        back.setGravity(Gravity.CENTER);
        nav.addView(back, frameParams(dp(36), dp(38), Gravity.START));
        TextView centered = label(title, 9, Color.WHITE, Typeface.BOLD);
        centered.setGravity(Gravity.CENTER);
        nav.addView(centered, frameParams(-1, dp(38), Gravity.CENTER));
        return nav;
    }

    private LinearLayout stepHeader(String step, String title, int progress) {
        LinearLayout area = new LinearLayout(this);
        area.setOrientation(LinearLayout.VERTICAL);
        area.setPadding(0, dp(4), 0, dp(12));
        LinearLayout row = new LinearLayout(this);
        row.setGravity(Gravity.CENTER_VERTICAL);
        TextView badge = label(progress == 1 ? "+" : "ID", 18, Color.WHITE, Typeface.BOLD);
        badge.setGravity(Gravity.CENTER);
        badge.setIncludeFontPadding(false);
        badge.setBackground(circle(PURPLE));
        row.addView(badge, new LinearLayout.LayoutParams(dp(42), dp(42)));
        LinearLayout titles = new LinearLayout(this);
        titles.setOrientation(LinearLayout.VERTICAL);
        titles.setPadding(dp(9), 0, 0, 0);
        titles.addView(label(step, 8, Color.rgb(178, 232, 204), Typeface.NORMAL));
        titles.addView(label(title, 10, Color.WHITE, Typeface.BOLD));
        row.addView(titles, new LinearLayout.LayoutParams(0, -2, 1));
        area.addView(row);
        ProgressBar progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        progressBar.setMax(4);
        progressBar.setProgress(progress);
        progressBar.setProgressTintList(ColorStateList.valueOf(PURPLE));
        progressBar.setProgressBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.rgb(142, 208, 177)));
        area.addView(progressBar, new LinearLayout.LayoutParams(-1, dp(4)));
        return area;
    }

    private TextView addField(LinearLayout card, String name, EditText input, String example, String validationMessage) {
        LinearLayout row = new LinearLayout(this);
        row.setGravity(Gravity.CENTER_VERTICAL);
        TextView fieldName = label(name, 10, INK, Typeface.NORMAL);
        row.addView(fieldName, new LinearLayout.LayoutParams(0, dp(39), 1));
        input.setHint(example);
        input.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
        row.addView(input, new LinearLayout.LayoutParams(dp(105), dp(39)));
        card.addView(row);
        View divider = new View(this);
        divider.setBackgroundColor(Color.rgb(239, 239, 239));
        card.addView(divider, new LinearLayout.LayoutParams(-1, 1));
        TextView message = label(validationMessage, 9, Color.rgb(205, 40, 53), Typeface.NORMAL);
        message.setGravity(Gravity.END);
        message.setPadding(0, 0, 0, dp(2));
        card.addView(message, new LinearLayout.LayoutParams(-1, -2));
        return message;
    }

    private void updateValidationMessages() {
        if (phoneMessage == null) return;
        boolean phoneValid = validator.validatePhone(text(phoneInput)) == null;
        boolean carnetValid = validator.validateCarnet(text(carnetInput)) == null;
        boolean complementValid = validator.validateComplement(text(complementInput)) == null;
        phoneMessage.setVisibility(phoneInput.hasFocus() && !phoneValid ? View.VISIBLE : View.GONE);
        carnetMessage.setVisibility(carnetInput.hasFocus() && !carnetValid ? View.VISIBLE : View.GONE);
        boolean complementHasText = !text(complementInput).isEmpty();
        complementMessage.setVisibility(complementInput.hasFocus() && complementHasText && !complementValid ? View.VISIBLE : View.GONE);
    }

    private void continueFromInformation() {
        UserData data = new UserData(text(phoneInput), text(carnetInput), text(complementInput));
        String error = validator.validate(data);
        if (error != null) {
            showError(error);
            return;
        }
        viewModel.setUserData(data);
        if (hasLocationPermission()) showAuthentication();
        else showLocationSheet();
    }

    private void showLocationSheet() {
        View dim = new View(this);
        dim.setBackgroundColor(Color.argb(125, 0, 0, 0));
        dim.setClickable(true);
        root.addView(dim, frameParams(-1, -1, Gravity.TOP));

        LinearLayout sheet = new LinearLayout(this);
        sheet.setOrientation(LinearLayout.VERTICAL);
        sheet.setGravity(Gravity.CENTER_HORIZONTAL);
        sheet.setPadding(dp(22), dp(20), dp(22), dp(14));
        sheet.setElevation(dp(10));
        sheet.setBackground(round(Color.WHITE, dp(17), dp(17), 0, 0));
        TextView warning = label("!", 28, Color.rgb(251, 211, 61), Typeface.BOLD);
        warning.setGravity(Gravity.CENTER);
        warning.setBackground(circle(Color.rgb(255, 249, 216)));
        sheet.addView(warning, new LinearLayout.LayoutParams(dp(62), dp(62)));
        TextView title = label("Activa tu ubicacion", 11, INK, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        sheet.addView(title, marginParams(-1, -2, 0, 12, 0, 0));
        TextView copy = label("Para continuar con la creacion de tu bille, es necesario que\nactives la ubicacion en tu dispositivo.", 8, MUTED, Typeface.NORMAL);
        copy.setGravity(Gravity.CENTER);
        sheet.addView(copy);
        Button continueButton = button("Continuar");
        sheet.addView(continueButton, marginParams(-1, dp(42), 0, 16, 0, 0));
        continueButton.setOnClickListener(v -> requestLocationPermission());
        root.addView(sheet, frameParams(-1, dp(255), Gravity.BOTTOM));
        root.bringChildToFront(sheet);
        sheet.startAnimation(new TranslateAnimation(0, 0, dp(255), 0));
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST && hasLocationPermission()) showAuthentication();
        else if (requestCode == LOCATION_REQUEST) Toast.makeText(this, "Necesitamos el permiso para continuar", Toast.LENGTH_SHORT).show();
    }

    private void showAuthentication() {
        root = new FrameLayout(this);
        root.setBackgroundColor(GREEN);
        LinearLayout page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.VERTICAL);
        header.setPadding(dp(16), dp(8), dp(16), 0);
        header.addView(topNavigation("Autenticacion"));
        header.addView(stepHeader("PASO 2 / 4", "Autenticacion", 2));
        page.addView(header, new LinearLayout.LayoutParams(-1, dp(190)));

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(16), dp(12), dp(16), dp(10));
        card.setBackground(round(Color.WHITE, dp(16), dp(16), 0, 0));
        card.addView(new SpeakerIcon(this), new LinearLayout.LayoutParams(-1, dp(28)));
        TextView notice = label("Antes de comenzar con la prueba de autenticacion,\nte recomendamos:", 11, MUTED, Typeface.BOLD);
        notice.setGravity(Gravity.CENTER);
        card.addView(notice);
        TextView instruction = label("Situate en un lugar con buena iluminacion.", 10, MUTED, Typeface.NORMAL);
        instruction.setGravity(Gravity.CENTER);
        card.addView(instruction, marginParams(-1, -2, 0, 8, 0, 0));
        ImageView peopleImage = new ImageView(this);
        peopleImage.setImageResource(R.drawable.dos_personas);
        peopleImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        peopleImage.setAdjustViewBounds(true);
        card.addView(peopleImage, new LinearLayout.LayoutParams(-1, 0, 1));
        LinearLayout dots = new LinearLayout(this);
        dots.setGravity(Gravity.CENTER);
        dots.addView(dot(true));
        dots.addView(dot(false));
        dots.addView(dot(false));
        dots.addView(dot(false));
        card.addView(dots, new LinearLayout.LayoutParams(-1, dp(24)));
        primaryButton = button("Siguiente");
        card.addView(primaryButton, new LinearLayout.LayoutParams(-1, dp(46)));
        primaryButton.setOnClickListener(v -> authenticate());
        page.addView(card, new LinearLayout.LayoutParams(-1, 0, 1));
        root.addView(page, frameParams(-1, -1, Gravity.TOP));
        setContentView(root);
        animateIn(root);
    }

    private TextView dot(boolean selected) {
        TextView dot = label("", 1, Color.TRANSPARENT, Typeface.NORMAL);
        dot.setBackground(circle(selected ? PURPLE : Color.LTGRAY));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dp(selected ? 8 : 6), dp(selected ? 8 : 6));
        params.setMargins(dp(4), 0, dp(4), 0);
        dot.setLayoutParams(params);
        return dot;
    }

    private void authenticate() {
        // Recheck immediately before consuming the service in case permission was revoked.
        if (!hasLocationPermission()) {
            showLocationSheet();
            return;
        }
        primaryButton.setEnabled(false);
        primaryButton.setText("Validando...");
        loading = new ProgressBar(this);
        root.addView(loading, frameParams(dp(34), dp(34), Gravity.CENTER));
        viewModel.authenticate(new BilleRepository.Callback() {
            @Override public void onSuccess() { showSuccess(); }
            @Override public void onError(String message) { showError(message); }
        });
    }

    private void showSuccess() {
        root.removeAllViews();
        LinearLayout page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setGravity(Gravity.CENTER);
        page.setPadding(dp(24), 0, dp(24), 0);

        TextView check = label("✓", 48, Color.WHITE, Typeface.BOLD);
        check.setGravity(Gravity.CENTER);
        check.setBackground(circle(GREEN));
        page.addView(check, new LinearLayout.LayoutParams(dp(94), dp(94)));

        TextView title = label("Bienvenido a bille", 25, INK, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        page.addView(title, marginParams(-1, -2, 0, 20, 0, 0));

        TextView subtitle = label("Tu identidad fue verificada correctamente.", 14, MUTED, Typeface.NORMAL);
        subtitle.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams subtitleParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        page.addView(subtitle, subtitleParams);

        root.setBackgroundColor(Color.WHITE);
        root.addView(page, frameParams(-1, -1, Gravity.CENTER));
        animateIn(page);
    }

    private EditText input(int type, int maxLength) {
        EditText input = new EditText(this);
        input.setTextSize(10);
        input.setTextColor(INK);
        input.setHintTextColor(Color.rgb(148, 148, 148));
        input.setSingleLine(true);
        input.setInputType(type);
        input.setPadding(0, 0, 0, 0);
        input.setBackgroundColor(Color.TRANSPARENT);
        input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
        return input;
    }

    private Button button(String text) {
        Button button = new Button(this);
        button.setText(text);
        button.setTextColor(Color.WHITE);
        button.setTextSize(13);
        button.setAllCaps(false);
        button.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        button.setBackground(round(GREEN, dp(6), dp(6), dp(6), dp(6)));
        return button;
    }

    private TextView label(String text, int size, int color, int style) {
        TextView view = new TextView(this);
        view.setText(text);
        view.setTextSize(size);
        view.setTextColor(color);
        view.setTypeface(Typeface.DEFAULT, style);
        return view;
    }

    private ColorStateList checkBoxColors() {
        return new ColorStateList(
                new int[][]{new int[]{android.R.attr.state_checked}, new int[]{}},
                new int[]{GREEN, Color.rgb(155, 160, 158)}
        );
    }

    private GradientDrawable round(int color, int topLeft, int topRight, int bottomRight, int bottomLeft) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadii(new float[]{topLeft, topLeft, topRight, topRight, bottomRight, bottomRight, bottomLeft, bottomLeft});
        return drawable;
    }

    private GradientDrawable circle(int color) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setShape(GradientDrawable.OVAL);
        return drawable;
    }

    private FrameLayout.LayoutParams frameParams(int width, int height, int gravity) {
        return frameParams(width, height, gravity, 0, 0, 0, 0);
    }

    private FrameLayout.LayoutParams frameParams(int width, int height, int gravity, int left, int top, int right, int bottom) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, height);
        params.gravity = gravity;
        params.setMargins(left, top, right, bottom);
        return params;
    }

    private LinearLayout.LayoutParams marginParams(int width, int height, int left, int top, int right, int bottom) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
        params.setMargins(left, top, right, bottom);
        return params;
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        if (primaryButton != null) {
            Animation shake = new TranslateAnimation(-dp(5), dp(5), 0, 0);
            shake.setDuration(70);
            shake.setRepeatCount(3);
            primaryButton.startAnimation(shake);
        }
    }

    private void animateIn(View view) {
        AlphaAnimation animation = new AlphaAnimation(0f, 1f);
        animation.setDuration(350);
        view.startAnimation(animation);
    }

    private boolean hasLocationPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private String text(EditText input) { return input.getText().toString().trim(); }
    private int dp(int value) { return Math.round(value * getResources().getDisplayMetrics().density); }

    private static final class AuthenticationIllustration extends View {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        AuthenticationIllustration(Context context) { super(context); }

        @Override protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            float w = getWidth();
            float h = getHeight();
            paint.setColor(Color.rgb(239, 249, 242));
            canvas.drawRect(w * .13f, h * .16f, w * .87f, h * .95f, paint);
            drawPerson(canvas, w * .35f, h * .55f, Color.rgb(81, 126, 103), Color.rgb(251, 196, 141));
            drawPerson(canvas, w * .66f, h * .55f, Color.rgb(73, 101, 83), Color.rgb(227, 172, 119));
            paint.setColor(Color.rgb(97, 194, 83));
            canvas.drawCircle(w * .39f, h * .42f, dp(7), paint);
            canvas.drawCircle(w * .70f, h * .42f, dp(7), paint);
            paint.setColor(Color.WHITE);
            paint.setTextSize(dp(8));
            canvas.drawText("✓", w * .375f, h * .445f, paint);
            canvas.drawText("✓", w * .685f, h * .445f, paint);
        }

        private void drawPerson(Canvas canvas, float x, float y, int shirt, int skin) {
            paint.setColor(shirt);
            canvas.drawRoundRect(x - dp(27), y + dp(40), x + dp(27), y + dp(112), dp(12), dp(12), paint);
            paint.setColor(skin);
            canvas.drawCircle(x, y + dp(17), dp(23), paint);
            paint.setColor(Color.rgb(65, 62, 54));
            canvas.drawArc(x - dp(23), y - dp(8), x + dp(23), y + dp(28), 180, 180, true, paint);
            paint.setColor(Color.rgb(40, 40, 40));
            canvas.drawCircle(x - dp(8), y + dp(20), dp(2), paint);
            canvas.drawCircle(x + dp(8), y + dp(20), dp(2), paint);
        }

        private int dp(int value) { return Math.round(value * getResources().getDisplayMetrics().density); }
    }

    private static final class SpeakerIcon extends View {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        SpeakerIcon(Context context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            float centerY = getHeight() / 2f;
            float right = getWidth() - dp(4);
            paint.setColor(GREEN);
            paint.setStyle(Paint.Style.FILL);
            Path speaker = new Path();
            speaker.moveTo(right - dp(22), centerY - dp(4));
            speaker.lineTo(right - dp(14), centerY - dp(4));
            speaker.lineTo(right - dp(5), centerY - dp(11));
            speaker.lineTo(right - dp(5), centerY + dp(11));
            speaker.lineTo(right - dp(14), centerY + dp(4));
            speaker.lineTo(right - dp(22), centerY + dp(4));
            speaker.close();
            canvas.drawPath(speaker, paint);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(dp(2));
            canvas.drawArc(right - dp(15), centerY - dp(11), right + dp(1), centerY + dp(11), -55, 110, false, paint);
            canvas.drawArc(right - dp(10), centerY - dp(16), right + dp(7), centerY + dp(16), -55, 110, false, paint);
        }

        private int dp(int value) { return Math.round(value * getResources().getDisplayMetrics().density); }
    }

    private static final class AlphanumericInputFilter implements InputFilter {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            StringBuilder accepted = new StringBuilder();
            for (int index = start; index < end; index++) {
                char character = source.charAt(index);
                if ((character >= 'A' && character <= 'Z')
                        || (character >= 'a' && character <= 'z')
                        || (character >= '0' && character <= '9')) {
                    accepted.append(character);
                }
            }
            return accepted.length() == end - start ? null : accepted;
        }
    }
}
